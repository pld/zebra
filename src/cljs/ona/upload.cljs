(ns ona.upload
  (:import goog.net.IframeIo)
  (:require [domina :as dom]
            [domina.events :as ev]
            [goog.events :as gev]))

(defn- upload-complete
  []
  (dom/add-class! (dom/by-id "file-uploading") "hidden"))

(defn- upload-error
  []
  (dom/remove-class! (dom/by-id "file-verified") "hidden"))

(defn uploader
  "Handle upload file via an IFrame. Send form data to "
  [form-id path]
  (let [io (IframeIo.)]
    (dom/add-class! (dom/by-id "file-choose") "hidden")
    (dom/set-text! (dom/by-id "filename")
                   (.-value (dom/by-id "file")))
    (dom/remove-class! (dom/by-id "file-uploading") "hidden")
    (gev/listen io
                (aget goog.net.EventType "SUCCESS")
                #(js/alert "SUCCESS!"))
    (gev/listen io
                (aget goog.net.EventType "ERROR")
                #(upload-error))
    (gev/listen io
                (aget goog.net.EventType "COMPLETE")
                #(upload-complete))
    (.setErrorChecker io #(not= "ok" (.getResponseText io)))
    (.sendFromForm io (dom/by-id form-id) path)))

(defn ^:export init
  "Send click events to the upload function."
  [upload-button-id form-id path]
  (ev/listen! (dom/by-id upload-button-id)
              :click (fn [event]
                       (uploader form-id path))))
