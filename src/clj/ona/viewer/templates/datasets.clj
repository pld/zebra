(ns ona.viewer.templates.datasets
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       do->
                                       first-of-type
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
  [dataset project-id]
  (u/dataset (:formid dataset)
             project-id))

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
  [:div#chart]
  [])

(defn- view-for-context
  "Return the view appropriate for the passed context."
  [context dataset]
  (condp = context
    :map (show-map)
    :table (apply show-table (clean-for-table dataset))
    ;; TODO make these views real
    :chart (show-chart)
    :photo (show-map)
    :activity (show-map)))

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
  [dataset-id project-id metadata dataset data-entry-link username context]

  ;; Page-title
  [:div.page-header [:div first-of-type] :h1] (content (:title metadata))

  ;; Top nav
  [:a.enter-data] (set-attr :href data-entry-link)
  [:div#username] (content (user-link username))
  [:a#sharing] (set-attr :href (u/dataset-sharing dataset-id project-id))
  [:a#download-all] (set-attr :href (u/dataset-download dataset-id))

  ;; View nav
  [:a#map-link](set-attr :href (u/dataset dataset-id project-id))
  [:a#table-link](set-attr :href (u/dataset-table dataset-id project-id))
  [:a#chart-link](set-attr :href (u/dataset-chart dataset-id project-id))
  [:a#photo-link](set-attr :href (u/dataset-photo dataset-id project-id))
  [:a#activity-link](set-attr :href (u/dataset-activity dataset-id project-id))

  ;; Sidenav
  [:div#sidenav [:p#description]] (content (:description metadata))
  [:div#sidenav [:a#form-source]] (do->
                                   (content (str (:id_string metadata)) ".xls")
                                   (set-attr :href (str "/")))
  [:div#dataset-activity] (content (activity dataset metadata))
  [:p.tagbox [:span.tag (but first-of-type)]] nil
  [:p.tagbox [:span.tag first-of-type]] (clone-for [tag (:tags metadata)]
                                                   [:span.tag] (content tag))
  [:span.rec] (content (str (count dataset) " records"))

  ;; Context
  [:div.dataset-context] (content (view-for-context context dataset)))

(defsnippet datasets-table "templates/dataset/list.html"
  [:#datasets-table]
  [datasets project-id profile]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [dataset datasets]
             [:.avatar] (set-attr :src (gravatar (:email profile)))
             [:.username] (content (:username profile))
             [:a.dataset-name] (do->
                                     (content (:title dataset))
                                     (set-attr :href (dataset-url dataset project-id)))
             [:ul.submenu :li.open :a] (set-attr
                                        :href (dataset-url dataset project-id))
             [:ul.submenu :li.settings :a] (set-attr :href
                                                     (u/dataset-settings
                                                      (:formid dataset)
                                                      project-id))

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
                                (:formid dataset)
                                (s/last-url-param (:url project))))))
             [:ul.submenu :li.replace] nil
             [:ul.submenu :li.copy] nil
             [:ul.submenu :li.rename] nil
             [:ul.submenu :li.download :a] (set-attr :href
                                                     (u/dataset-download
                                                      (:formid dataset)))
             [:ul.submenu :li.delete :a] (set-attr :href
                                                   (u/dataset-delete
                                                    (:formid dataset)))
             [:ul.submenu :li.cancel] nil
             [:span.rec] (content (num-submissions-str dataset))
             [:span.t-state] (content (if (:public_data dataset)
                                        "Public"
                                        "Private"))))
