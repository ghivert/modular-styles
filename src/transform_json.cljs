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

(defn replace-extension-by [language file-path]
  (-> file-path
      (remove-extension)
      (str "." language)))

(defn capitalize-first [[first & rest]]
  (cons (string/capitalize first) rest))

(defn capitalize-file-name [language file-name]
  (if (or (= language "purs") (= language "elm"))
    (let [res (string/split file-name #"/")]
      (->> res
           (map string/capitalize)
           (string/join "/")))
    file-name))

(defn get-full-path! [files-path dest-path css-file-name language]
  (->> css-file-name
       (relative-path files-path)
       (replace-extension-by language)
       (capitalize-file-name language)
       (.resolve path dest-path)))

(defn render-namespace-and-content [package-path file-content]
  (as-> package-path v
        (string/join "" ["(ns " v ")"])
        (string/join "\n\n" [v file-content])
        (str v "\n")))

(defn render-module-and-content [package-path file-content]
  (as-> package-path v
        (string/join "" ["module " v " exposing (..)"])
        (string/join "\n\n" [v file-content])
        (str v "\n")))

(defn render-purescript-module-and-content [package-path file-content]
  (as-> package-path v
        (string/join "" ["module " v " where"])
        (string/join "\n\n" [v file-content])
        (str v "\n")))

(defn turn-to-clojure-package-path [file-path]
  (-> file-path
      (string/replace #"/" ".")
      (string/replace #"_" "-")))

(defn turn-to-elm-package-path [file-path]
  (as-> file-path v
        (string/split v #"/")
        (map string/capitalize v)
        (string/join "." v)))

(defn turn-to-purescript-package-path [file-path]
  (as-> file-path v
        (string/split v #"/")
        (map string/capitalize v)
        (string/join "." v)))

(defn path-to-cljs-package-path [source-path full-path]
  (-> source-path
      (relative-path full-path)
      (remove-extension)
      (turn-to-clojure-package-path)))

(defn path-to-elm-package-path [source-path full-path]
  (-> source-path
      (relative-path full-path)
      (remove-extension)
      (turn-to-elm-package-path)))

(defn path-to-purescript-package-path [source-path full-path]
  (-> source-path
      (relative-path full-path)
      (remove-extension)
      (turn-to-purescript-package-path)))

(defn json-classes-to-clojure-def [[class-name class-module-name]]
  (str "(def " class-name " \"" class-module-name "\")"))

(defn json-classes-to-elm-const [[class-name class-module-name]]
  (let [sig (str class-name " : String")
        decl (str class-name " = \"" class-module-name "\"\n")]
    (string/join "\n" [sig decl])))

(defn json-classes-to-purescript-const [[class-name class-module-name]]
  (let [sig (str class-name " :: String")
        decl (str class-name " = \"" class-module-name "\"\n")]
    (string/join "\n" [sig decl])))

(defn render-complete-generic-file [mapper render-content]
  (fn [source-path json full-path]
    (->> json
         (js->clj)
         (map mapper)
         (string/join "\n")
         (render-content source-path full-path))))

(def render-complete-clojure-file
  (render-complete-generic-file
   json-classes-to-clojure-def
   (fn [source-path full-path json]
     (render-namespace-and-content
      (path-to-cljs-package-path source-path full-path) json))))

(def render-complete-elm-file
  (render-complete-generic-file
   json-classes-to-elm-const
   (fn [source-path full-path json]
     (render-module-and-content
      (path-to-elm-package-path source-path full-path) json))))

(def render-complete-purescript-file
  (render-complete-generic-file
   json-classes-to-purescript-const
   (fn [source-path full-path json]
     (render-purescript-module-and-content
      (path-to-purescript-package-path source-path full-path) json))))

(defn render-complete-file [source-path json language full-path]
  (prn language)
  (condp = language
    "elm" (render-complete-elm-file source-path json full-path)
    "cljs" (render-complete-clojure-file source-path json full-path)
    "purs" (render-complete-purescript-file source-path json full-path)
    (render-complete-clojure-file source-path json full-path)))

(defn get-json! [source-path files-path dest-path language]
  (fn [css-file-name json]
    (let [full-path (get-full-path! files-path dest-path css-file-name language)]
      (->> full-path
           (render-complete-file source-path json language)
           (write-file! full-path)))))
