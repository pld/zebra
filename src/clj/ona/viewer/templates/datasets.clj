(ns ona.viewer.templates.datasets
  (:use [net.cgrand.enlive-html :only [append
                                       clone-for
                                       content
                                       defsnippet
                                       deftemplate
                                       do->
                                       first-of-type
                                       html
                                       set-attr
                                       nth-of-type
                                       but]
         :rename {html enlive-html}] :reload))

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
