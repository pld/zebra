(ns ona.upload
  (:import goog.net.IframeIo)
  (:require [domina :as dom]
            [domina.events :as ev]
            [goog.events :as gev]))

(defn- set-href
  [id value]
  (dom/set-attr! (dom/by-id id) "href" value))

(defn- upload-complete
  []
  (dom/add-class! (dom/by-id "file-uploading") "hidden"))

(defn- upload-error
  [response-text]
  (dom/set-text! (dom/by-id "error") response-text)
  (dom/remove-class! (dom/by-id "file-choose") "hidden")
  (dom/remove-class! (dom/by-id "file-error") "hidden"))

(defn- upload-success
  [response-text]
  (let [parsed-response (JSON/parse response-text)]
    (set-href "preview" (aget parsed-response "preview-url"))
    (set-href "delete" (aget parsed-response "delete-url"))
    (set-href "settings" (aget parsed-response "settings-url")))
  (dom/remove-class! (dom/by-id "file-verified") "hidden"))

(defn- error-returned?
  "Check if an error was returned by "
  [io]
  (try
    (not (aget (JSON/parse (.getResponseText io)) "settings-url"))
    (catch js/SyntaxError e
      true)))

(defn uploader
  "Handle upload file via an IFrame. Send form data to "
  [form-id path]
  (let [io (IframeIo.)]
    (dom/add-class! (dom/by-id "file-choose") "hidden")
    (dom/add-class! (dom/by-id "file-error") "hidden")
    (dom/set-text! (dom/by-class "filename")
                   (.-value (dom/by-id "file")))
    (dom/remove-class! (dom/by-id "file-uploading") "hidden")
    (gev/listen io
                (aget goog.net.EventType "SUCCESS")
                #(upload-success (.getResponseText io)))
    (gev/listen io
                (aget goog.net.EventType "ERROR")
                #(upload-error (.getResponseText io)))
    (gev/listen io
                (aget goog.net.EventType "COMPLETE")
                #(upload-complete))
    (.setErrorChecker io #(error-returned? io))
    (.sendFromForm io (dom/by-id form-id) path)))

(defn ^:export init
  "Send click events to the upload function."
  [upload-button-id form-id path]
  (ev/listen! (dom/by-id upload-button-id)
              :click (fn [event]
                       (uploader form-id path))))
