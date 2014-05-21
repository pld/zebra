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
                                       but]
         :rename {html enlive-html}] :reload))

(defsnippet home-content "templates/home.html"
   [:body :div#content]
   [items username]
   [:#username](content username)
   [:#datasets-table [:tr (but first-of-type)]] nil
   [:#datasets-table [:tr first-of-type]] (clone-for [item items]
                                                     [:tr (nth-of-type 2) :strong] (content (:item-name item)))
   [:#sidenav [:a first-of-type]] (set-attr :href "/dataset"))
