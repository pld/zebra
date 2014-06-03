(ns ona.viewer.templates.list-items
  (:use [net.cgrand.enlive-html :only [clone-for
                                       content
                                       defsnippet
                                       do->
                                       set-attr]] :reload))

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
                            (content (:name item))
                            (set-attr :href (str url (:id item))))
                  [:p] (if-not (:id item)
                         (content (:name item))
                         identity)
                  [:p :span.actions :a] (render-actions item url)))
