(ns ona.viewer.templates.home
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
                                       but
                                       at]
         :rename {html enlive-html}]
         [ona.viewer.templates.modals :only [share-dialog]]:reload))

(defsnippet home-content "templates/home.html"
  [:body :div#content]
  [username datasets]
  [:#username](content username)
  [:#datasets-table [:tr (but first-of-type)]] nil
  [:#datasets-table [:tr first-of-type]]
  (clone-for [dataset datasets]
             [:tr (nth-of-type 2) :strong] (content (:title dataset))
             [:ul.submenu [:li first-of-type] :a](set-attr
                                                   :href
                                                   (str "dataset/" (:formid dataset)))
             [:span.rec](content (str (:num_of_submissions dataset) " records")))

  ;; Set sidenav links
  [:#sidenav [:a first-of-type]] (set-attr :href "/dataset")
  [:#sidenav [:a (nth-of-type 2)]] (set-attr :href "/projects")
  [:.modal-wrap](append (share-dialog)))
