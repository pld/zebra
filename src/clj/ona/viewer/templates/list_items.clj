(ns ona.viewer.templates.list-items
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

(defn render-actions
  "Render the actions for a list item."
  [item url-base]
  (if-let [actions (:actions item)]
    (clone-for [action actions]
               (let [url-prefix (str url-base (:item-id item))
                     url (if-let [suffix (:url action)]
                           (str url-prefix "/" suffix)
                           url-prefix)]
                 [:a] (do->
                        (content (str " >> " (:name action)))
                        (set-attr :href url))))))

(defsnippet list-items "templates/list-items.html"
  [:body :div.content :> :.list-items]
  [items url]
  [:p] (clone-for [item items]
                  [:p :a] (do->
                            (content (:item-name item))
                            (set-attr :href (str url (:item-id item))))
                  [:p] (if-not (:item-id item)
                         (content (:item-name item))
                         identity)
                  [:p :span.actions :a] (render-actions item url)))