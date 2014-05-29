(ns ona.viewer.templates.datasets
  (:use [net.cgrand.enlive-html :only [but
                                       clone-for
                                       content
                                       defsnippet
                                       first-of-type
                                       nth-of-type
                                       set-attr]] :reload))

(defsnippet new-dataset "templates/dataset-new.html"
  [:body :div#content]
  [])

(defsnippet show "templates/vitamin-a.html"
  [:body :div#content]
  [metadata dataset]

  ;;Page-title
  [:div.page-header [:div first-of-type] :h1](content (:title metadata))

  ;;Sidenav
  [:div#sidenav [:p (nth-of-type 2)]](content (:description metadata))
  [:p.tagbox [:span.tag (but first-of-type)]] nil
  [:p.tagbox [:span.tag first-of-type]](clone-for [tag (:tags metadata)]
                                                               [:span.tag] (content tag))
  [:span.rec](content (str (count dataset) " records")))

(defsnippet datasets-table "templates/home.html"
  [:#datasets-table]
  [datasets]
  [:tbody [:tr (but first-of-type)]] nil
  [:tbody [:tr first-of-type]]
  (clone-for [dataset datasets]
             [:tr (nth-of-type 2) :strong] (content (:title dataset))
             [:ul.submenu [:li first-of-type] :a](set-attr
                                                   :href
                                                   (str "dataset/" (:formid dataset)))
             [:span.rec](content (str (:num_of_submissions dataset) " records"))))
