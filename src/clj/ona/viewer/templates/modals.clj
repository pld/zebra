(ns ona.viewer.templates.modals
  (:use [net.cgrand.enlive-html :only [defsnippet
                                       clone-for
                                       content
                                       first-of-type]] :reload))

(defsnippet share-dialog "templates/home.html"
  [:body :div#share_dialog]
  [datasets]
  [:div.modal-body :ul.form-list [:li (but first-of-type)]] nil
  [:div.modal-body :ul.form-list [:li first-of-type]] (clone-for
                                                        [dataset datasets]
                                                        [:strong] (content (:title dataset))))
