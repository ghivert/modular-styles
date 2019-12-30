(ns bin
  (:require ["commander" :as program]
            [main]))

(.version program "0.0.2")

(-> program
    (.command "compile")
    (.option "--files <filesPath>" "Source path for files.")
    (.option "--dest <destPath>", "Destination path for interfaces.")
    (.option "--source <sourcePath>", "Source path for project.")
    (.option "--extension <extension>", "Stylesheets extension. Defaults to css.")
    (.option "--tempCSS <tempCSS>", "Path for temporary stylesheets.")
    (.option "--bundleName <bundleName>", "Name for resulting CSS bundle. Defaults to styles.css.")
    (.option "--bundlePath <bundleCSSPath>", "Path for resulting CSS bundle. Defaults to public.")
    (.option "--language <language>", "Select language into which modules will be generated. Defaults to cljs.")
    (.action (fn [options] (main/compile options))))

(-> program
    (.command "watch")
    (.option "--files <filesPath>" "Source path for files.")
    (.option "--dest <destPath>" "Destination path for interfaces.")
    (.option "--source <sourcePath>" "Source path for project.")
    (.option "--extension <extension>" "Stylesheets extension. Defaults to css.")
    (.option "--tempCSS <tempCSS>" "Path for temporary stylesheets.")
    (.option "--bundleName <bundleName>" "Name for resulting CSS bundle. Defaults to styles.css.")
    (.option "--bundlePath <bundleCSSPath>" "Path for resulting CSS bundle. Defaults to public.")
    (.option "--language <language>" "Select language into which modules will be generated. Defaults to cljs.")
    (.action (fn [options] (main/watch options))))

(.parse program (.-argv js/process))
