(ns ona.viewer.templates.datasets
  (:use [net.cgrand.enlive-html :only [append
                                       but
                                       clone-for
                                       content
                                       defsnippet
                                       do->
                                       first-of-type
                                       html
                                       nth-of-type
                                       set-attr]]
        :reload
        [ona.utils.numeric :only [pluralize-number]]
        [clavatar.core :only [gravatar]]
        [clojure.string :only [join]])
  (:require [ona.viewer.urls :as u]
            [ona.utils.time :as t]
            [ona.utils.string :as s]))

(def hidden-column-prefix \_)

(defn- filter-hidden-columns
  "Remove hidden columns from a dataset map."
  [dataset]
  (map
   #(select-keys % (for [[k v] %
                         :when (not=
                                (-> k name first)
                                \_)] k)) dataset))

(defn- num-submissions-str
  [dataset]
  (let [num-submissions (:num_of_submissions dataset)]
    (str (if (< num-submissions 0) 0 num-submissions)
         " records")))

(defn- clean-for-table
  "Take a dataset hash and return headers and rows lists."
  [dataset]
  (let [filtered-dataset (filter-hidden-columns dataset)
        sorted-dataset (map #(into (sorted-map) %) filtered-dataset)]
    [(map name (-> sorted-dataset first keys))
     (map vals sorted-dataset)]))

(defn- latest-submission-str
  "String for the latest submission made."
  [metadata]
  (if-let [interval (t/date->days-ago-str (:last_submission_time metadata))]
    (join " " ["Latest around"
               interval
               "ago."])
    "No submissions made."))

(defn- submission-made-str
  "String for the number of submissions made."
  [dataset]
  (let [no-submission (t/get-no-submissions-today dataset)]
    (str (pluralize-number no-submission "submission")
         " made today.")))

(defn- dataset-url
  [owner dataset project-id]
  (u/dataset owner
             project-id
             (:formid dataset)))


(defsnippet new-dataset "templates/dataset/new.html"
  [:body :div#content]
  [project]
  [:span#project-name] (content (:name project)))

(defsnippet show-table "templates/dataset/table.html"
  [:table#submissions]
  [headers rows]
  [:thead [:th (but first-of-type)]] nil
  [:tbody [:tr (but first-of-type)]] nil
  [:thead [:th first-of-type]] (clone-for [header headers]
                                          [:th] (content header))
  [:tbody [:tr first-of-type]]
  (clone-for [row rows]
             [:tr [:td (but first-of-type)]] nil
             [:tr [first-of-type]] (clone-for [v row]
                                              [:td] (content (str v)))))

(defsnippet show-map "templates/dataset/show.html"
  [:div#map]
  []
  [:div#map [:img]] nil)

(defsnippet show-chart "templates/dataset/show.html"
  [:div.charts]
  [data]
  [:div.charts] (clone-for [data-item data]
                  [:h3.chart-name] (content (:label data-item))
                  [:div.bar-chart] (-> data-item :chart html content)))

(defmulti view-for-context
  "Return the view appropriate for the passed context."
  (fn [context dataset-details] context))

(defmethod view-for-context :default [_ data]
  (show-map))

(defmethod view-for-context :chart [_ data]
  (show-chart (:charts data)))

(defmethod view-for-context :table [_ data]
  (apply show-table (clean-for-table (:dataset data))))

(defsnippet user-link "templates/dataset/show.html"
  [:div#user-link ]
  [username]
  [:a#user-profile] (set-attr :href (u/profile username))
  [:span#user-name] (content username))

(defsnippet activity "templates/dataset/show.html"
  [:p#activity]
  [dataset metadata]
  [:span#submissions] (content (submission-made-str dataset))
  [:span#latest] (content (latest-submission-str metadata)))

(defsnippet show "templates/dataset/show.html"
  [:body :div#content]
  [owner project-id dataset-id dataset-details username context]
  ;; Page-title
  [:a#project-link] (do-> (content owner)
                          (set-attr :href (u/project-show owner project-id)))
  [:a#dataset-link] (do-> (content (-> dataset-details :metadata :title))
                          (set-attr :href (u/dataset owner project-id dataset-id)))

  ;; Top nav
  [:a.enter-data] (set-attr :href (:data-entry-link dataset-details))
  [:div#username] (content (user-link username))
  [:a#sharing] (set-attr :href (u/dataset-settings owner project-id dataset-id))
  [:a#download-all] (set-attr :href (u/dataset-download owner project-id dataset-id))

  ;; View nav
  [:a#map-link](set-attr :href (u/dataset owner project-id dataset-id))
  [:a#table-link](set-attr :href (u/dataset-table owner project-id dataset-id))
  [:a#chart-link](set-attr :href (u/dataset-chart owner project-id dataset-id))
  [:a#photo-link](set-attr :href (u/dataset-photo owner project-id dataset-id))
  [:a#activity-link](set-attr :href (u/dataset-activity owner project-id dataset-id))

  ;; Sidenav
  [:div#sidenav [:p#description]] (content (-> dataset-details :metadata :description))
  [:div#sidenav [:a#form-source]] (do->
                                   (content (-> dataset-details
                                                :metadata
                                                :id_string)
                                            ".xls")
                                   (set-attr :href "/"))
  [:div#dataset-activity] (content (activity (:dataset dataset-details)
                                             (:metadata dataset-details)))
  [:a#sharing-settings] (set-attr :href (u/dataset-sharing owner
                                                           project-id
                                                           dataset-id))
  [:p.tagbox [:span.tag (but first-of-type)]] nil
  [:p.tagbox [:span.tag first-of-type]] (clone-for [tag (-> dataset-details
                                                            :metadata
                                                            :tags)]
                                                   [:span.tag] (content tag))
  [:span.rec] (content (str (count (:dataset dataset-details)) " records"))

  ;; Context
  [:div.dataset-context] (content (view-for-context context dataset-details)))

(defsnippet datasets-table "templates/dataset/list.html"
  [:#datasets-table]
  [datasets owner project-id profile]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [dataset datasets]
             [:.avatar] (set-attr :src (gravatar (:email profile)))
             [:a.dataset-name] (do->
                                     (content (:title dataset))
                                     (set-attr :href (dataset-url owner
                                                                  dataset
                                                                  project-id)))
             [:ul.submenu :li.open :a] (set-attr
                                        :href (dataset-url owner dataset project-id))
             [:ul.submenu :li.settings :a] (set-attr :href
                                                     (u/dataset-settings
                                                      owner
                                                      project-id
                                                      (:formid dataset)))

             [:ul.submenu :li.move] nil
             [:ul.submenu :li.star] nil
             [:ul.submenu :li.transfer] nil
             [:ul.submenu :li.folder :li.project-list]
             (clone-for [project (:projects profile)]
                        [:a] (do->
                              (content (:name project))
                              (set-attr
                               :href
                               (u/dataset-move
                                owner
                                (-> project :url s/last-url-param)
                                (:formid dataset)))))
             [:ul.submenu :li.replace] nil
             [:ul.submenu :li.copy] nil
             [:ul.submenu :li.rename] nil
             [:ul.submenu :li.download :a] (set-attr :href
                                                     (u/dataset-download
                                                      owner
                                                      project-id
                                                      (:formid dataset)))
             [:ul.submenu :li.delete :a] (set-attr :href
                                                   (u/dataset-delete
                                                    owner
                                                    project-id
                                                    (:formid dataset)))
             [:ul.submenu :li.cancel] nil
             [:span.rec] (content (num-submissions-str dataset))
             [:span.t-state] (content (if (:public_data dataset)
                                        "Public"
                                        "Private"))
             [:p#latest-record] (content (latest-submission-str dataset))))
