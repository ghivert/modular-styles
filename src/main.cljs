(ns main
  (:require ["path" :as path]
            ["fs" :as fs]
            ["gulp" :as gulp]
            ["gulp-postcss" :as postcss]
            ["gulp-concat" :as gconcat]
            ["gulp-debug" :as debug]
            ["gulp-plumber" :as plumber]
            ["gulp-if" :as gif]
            ["cssnano" :as cssnano]
            ["import-cwd" :as import-cwd]
            [goog.object :refer [get]]
            [transform-json]))

(defn get-file [file-path]
  (try
    (js/require file-path)
    (catch :default error nil)))

(defn get-config []
  (js->clj (or (get-file (path/resolve ".postcssrc.js"))
               (get-file (path/resolve ".postcss.config.js"))
               (get-file (path/resolve ".postcssrc.json"))
               #js {})))

(defn use-plugins [plugins]
  (let [require-postcss (fn [[plugin options]]
                          ((import-cwd plugin) (clj->js options)))]
    (mapv require-postcss plugins)))

(defn postcss-modules! [{:keys [source-path files-path dest-path language]}]
  (let [postcss-modules (js/require "postcss-modules")]
    (->> (transform-json/get-json! source-path files-path dest-path language)
         (hash-map :getJSON)
         (clj->js)
         (postcss-modules))))

(defn compute-options! [options]
  (let [config (get-config)
        plugins (use-plugins (or (get config "plugins") {}))
        converted-plugins (into [] (concat [(postcss-modules! options)] plugins))]
    (assoc config "plugins" converted-plugins)))

(def all-files-plugins [(cssnano)])

(defn css! [options]
  (let [{:keys [files-path extension temp-css bundle-name bundle-path]} options
        {:strs [plugins] :as options} (compute-options! options)
        gulp-if-condition (not (not temp-css))]
    (fn []
      (-> gulp
          (.src (path/resolve files-path (str "**/*." (or extension "css"))))
          (.pipe (debug (clj->js {:title "Test"})))
          (.pipe (plumber))
          (.pipe (debug (clj->js {:title "Beginning compiling CSS Modules..."})))
          (.pipe (postcss (clj->js plugins) (clj->js (dissoc options "plugins"))))
          (.pipe (gif gulp-if-condition (.dest gulp (path/resolve (or temp-css "")))))
          (.pipe (gconcat (or bundle-name "styles.css")))
          (.pipe (postcss (clj->js all-files-plugins)))
          (.pipe (.dest gulp (path/resolve (or bundle-path "./public"))))
          (.pipe (debug (clj->js {:title "CSS Modules compilation finished!"})))))))

(defn styles [options]
  (fn [] (.watch gulp (:files-path options) (css! options))))

(defn convert-options [options]
  (if-not (map? options)
    {:source-path (or (get options "source") (get options "sourcePath"))
     :files-path (or (get options "files") (get options "filesPath"))
     :dest-path (or (get options "dest") (get options "destPath"))
     :extension (get options "extension")
     :bundle-name (get options "bundleName")
     :bundle-path (get options "bundlePath")
     :temp-css (get options "tempCSS")
     :language (get options "language")}
    options))

(defn compile [options]
  (let [opts (convert-options options)]
    (.task gulp "css" (css! opts))
    ((.task gulp "css"))))

(defn watch [options]
  (let [opts (convert-options options)]
    (.task gulp "watch-styles" (.series gulp (css! opts) (styles opts)))
    ((.task gulp "watch-styles"))))
