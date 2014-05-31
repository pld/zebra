(ns ona.viewer.templates.datasets
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       do->
                                       first-of-type
                                       nth-of-type
                                       set-attr]] :reload))

(defsnippet new-dataset "templates/dataset-new.html"
  [:body :div#content]
  [])

(defsnippet show "templates/show.html"
  [:body :div#content]
  [metadata dataset data-entry-link]

  ;; Page-title
  [:div.page-header [:div first-of-type] :h1] (content (:title metadata))

  ;; Top nav
  [:a#enter-data] (set-attr :href data-entry-link)

  ;; Sidenav
  [:div#sidenav [:p (nth-of-type 2)]] (content (:description metadata))
  [:p.tagbox [:span.tag (but first-of-type)]] nil
  [:p.tagbox [:span.tag first-of-type]] (clone-for [tag (:tags metadata)]
                                                   [:span.tag] (content tag))
  [:span.rec](content (str (count dataset) " records")))

(defsnippet datasets-table "templates/home.html"
  [:#datasets-table]
  [datasets]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [dataset datasets]
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
                                            (str "/dataset/" (:formid dataset) "/download"))
             [:ul.submenu :li.delete :a] (set-attr
                                       :href
                                       (str "/dataset/" (:formid dataset) "/delete"))
             [:ul.submenu :li.cancel] nil
             [:span.rec](content (str (if (< (:num_of_submissions dataset) 0)
                                        0
                                        (:num_of_submissions dataset))
                                      " records"))
             [:span.t-state] (content (if (:public_data dataset)
                                        "Public"
                                        "Private"))))
