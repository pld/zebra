(ns ona.core
  (:import goog.net.IframeIo)
  (:require [domina :as dom]
            [domina.events :as ev]
            [goog.events :as gev]))

(defn upload
  "Handle upload file via an IFrame. Send form data to "
  []
  (let [form-id "form"
        upload-path "/upload"
        io (IframeIo.)]
    (gev/listen io
                (aget goog.net.EventType "SUCCESS")
                #(js/alert "SUCCESS!"))
    (gev/listen io
                (aget goog.net.EventType "ERROR")
                #(js/alert "ERROR!"))
    (gev/listen io
                (aget goog.net.EventType "COMPLETE")
                #(js/alert "COMPLETE!"))
    (.setErrorChecker io #(not= "ok" (.getResponseText io)))
    (.sendFromForm io (dom/by-id form-id) upload-path)))

(defn ^:export init
  "Send click events to the upload function."
  []
  (let [upload-button-id "upload-button"]
    (ev/listen! (dom/by-id upload-button-id)
                :click upload)))
