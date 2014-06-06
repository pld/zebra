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
        [ona.utils.numeric :only [pluralize-number]])
  (:require [ona.utils.time :as t]))

(defn- latest-submission-str
  "String for the latest submission made."
  [metadata]
  (if-let [interval (t/date->days-ago-str (:last_submission_time metadata))]
    (str "Latest around "
         interval
         " ago.")
    "No submissions made."))

(defn- submission-made-str
  "String for the number of submissions made."
  [dataset]
  (let [no-submission (t/get-no-submissions-today dataset)]
    (str (pluralize-number no-submission "submission")
         " made today.")))

(defsnippet new-dataset "templates/dataset-new.html"
  [:body :div#content]
  [project]
  [:span#project-name] (content (:name project)))

(defsnippet show-table "templates/show-table.html"
  [:table#submissions]
  [dataset]
  [:thead [:th (but first-of-type)]] nil
  [:tbody [:tr (but first-of-type)]] nil
  [:thead [:th first-of-type]] (clone-for [key (keys (first dataset))]
                                [:th] (content (str key)))
  [:tbody [:tr first-of-type]] (clone-for [submission dataset]
                                [:tr [:td (but first-of-type)]] nil
                                [:tr [first-of-type]] (clone-for [key (keys (first dataset))]
                                         [:td] (content (str (get submission key))))))

(defsnippet show-map "templates/show.html"
  [:div#map]
  [])

(defsnippet show "templates/show.html"
  [:body :div#content]
  [dataset-id metadata dataset data-entry-link username context]

  ;; Page-title
  [:div.page-header [:div first-of-type] :h1] (content (:title metadata))

  ;; Top nav
  [:a.enter-data] (set-attr :href data-entry-link)
  [:a#user-profile] (set-attr :href (str "/profile/" username))
  [:span#user-name] (content username)
  [:a#download-all] (set-attr :href (str "/dataset/" dataset-id "/download"))
  [:a#table](set-attr :href (str "/dataset/" dataset-id "/show/table"))

  ;; Sidenav
  [:div#sidenav [:p#description]] (content (:description metadata))
  [:div#sidenav [:a#form-source]] (do->
                                   (content (str (:id_string metadata)) ".xls")
                                   (set-attr :href (str "/")))
  [:p.activity :span.submissions] (content (submission-made-str dataset))
  [:p.activity :span.latest] (content (latest-submission-str metadata))
  [:p.tagbox [:span.tag (but first-of-type)]] nil
  [:p.tagbox [:span.tag first-of-type]] (clone-for [tag (:tags metadata)]
                                                   [:span.tag] (content tag))
  [:span.rec] (content (str (count dataset) " records"))

  ;; Context
  [:div.dataset-context] (content (if (= context "table")
                                    (show-table dataset)
                                    (show-map))))

(defsnippet datasets-table "templates/home.html"
  [:#datasets-table]
  [datasets username]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [dataset datasets]
             [:.username] (content username)
             [:tr (nth-of-type 2) :strong] (content (:title dataset))
             [:ul.submenu :li.open :a] (set-attr
                                        :href
                                        (str "/dataset/" (:formid dataset)))
             [:ul.submenu :li.share] nil
             [:ul.submenu :li.move] nil
             [:ul.submenu :li.star] nil
             [:ul.submenu :li.transfer] nil
             [:ul.submenu :li.folder] nil
             [:ul.submenu :li.replace] nil
             [:ul.submenu :li.copy] nil
             [:ul.submenu :li.rename] nil
             [:ul.submenu :li.download :a] (set-attr
                                            :href
                                            (str "/dataset/"
                                                 (:formid dataset)
                                                 "/download"))
             [:ul.submenu :li.delete :a] (set-attr
                                       :href
                                       (str "/dataset/" (:formid dataset) "/delete"))
             [:ul.submenu :li.cancel] nil
             [:span.rec] (content (str (if (< (:num_of_submissions dataset) 0)
                                        0
                                        (:num_of_submissions dataset))
                                      " records"))
             [:span.t-state] (content (if (:public_data dataset)
                                        "Public"
                                        "Private"))))
