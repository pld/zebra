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

  [:div.page-header [:div first-of-type] :h1](content (:title metadata))
  [:div#sidenav [:p (nth-of-type 2)]](content (:description metadata))
  )
