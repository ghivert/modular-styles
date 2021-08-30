(ns main-test
  (:require [cljs.test :refer (deftest is async)]
            [main]))

(deftest a-testing-test
  (async done
         (main/compile {:source-path "styles"
                        :dest-path "styles"
                        :files-path "src/test"})
         (js/setTimeout done 10000)))

(deftest test-sass
  (async done
         (main/compile {:source-path "styles"
                        :dest-path "styles"
                        :extension "scss"
                        :sass-config {}
                        :bundle-name "scss-styles.css"
                        :files-path "src/test"})
         (js/setTimeout done 10000)))
