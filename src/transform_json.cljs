(ns transform-json
  (:require ["path" :as path]
            ["fs" :as fs]
            [clojure.string :as string]))

(defn relative-path [fst snd]
  (.relative path fst snd))

(defn create-folder-sync! [dirname]
  (try (.accessSync fs dirname)
    (catch :default error
      (.mkdirSync fs dirname (clj->js {:recursive true})))))

(defn write-file! [full-path complete-file]
  (->> full-path
       (.dirname path)
       (create-folder-sync!))
  (.writeFileSync fs full-path complete-file))

(defn remove-extension [file-path]
  (string/replace file-path #".[^/.]+$" ""))

(defn replace-extension-by-cljs [file-path]
  (-> file-path
      (remove-extension)
      (str ".cljs")))

(defn get-full-path! [files-path dest-path css-file-name]
  (->> css-file-name
       (relative-path files-path)
       (replace-extension-by-cljs)
       (.resolve path dest-path)))

(defn render-namespace-and-content [package-path file-content]
  (as-> package-path v
        (string/join "" ["(ns " v ")"])
        (string/join "\n\n" [v file-content])
        (str v "\n")))

(defn turn-to-package-path [file-path]
  (string/replace file-path #"/" "."))

(defn path-to-package-path [source-path full-path]
  (-> source-path
      (relative-path full-path)
      (remove-extension)
      (turn-to-package-path)))

(defn json-classes-to-clojure-def [[class-name class-module-name]]
  (str "(def " class-name " \"" class-module-name "\")"))

(defn render-complete-file [source-path json full-path]
  (->> json
       (js->clj)
       (map json-classes-to-clojure-def)
       (string/join "\n")
       (render-namespace-and-content
        (path-to-package-path source-path full-path))))

(defn get-json [source-path files-path dest-path]
  (fn [css-file-name json]
    (let [full-path (get-full-path! files-path dest-path css-file-name)]
      (->> full-path
           (render-complete-file source-path json)
           (write-file! full-path)))))
