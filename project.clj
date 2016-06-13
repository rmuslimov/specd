(defproject specd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [http-kit "2.1.9"]
                 [org.danielsz/system "0.3.1-SNAPSHOT"]
                 [com.stuartsierra/component "0.3.1"]
                 [reloaded.repl "0.2.1"]
                 [environ "1.0.3"]
                 [compojure "1.5.0"]
                 [figwheel-sidecar "0.5.4-SNAPSHOT"]
                 [com.cemerick/piggieback "0.2.1"]]
  :main ^:skip-aot specd.core
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :target-path "target/%s"
  :profiles {:dev {:source-paths ["dev"]}
             :uberjar {:aot :all}}
  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-environ "1.0.3"]
            [lein-figwheel "0.5.4-SNAPSHOT"]]
  :figwheel {:css-dirs ["resources/public/css"]}
  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src" "dev"]
                :compiler {:optimizations :none
                           :output-to "dev-resources/public/main.js"
                           :pretty-print false}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:optimizations :advanced
                           :output-to "resources/public/main.js"
                           :pretty-print false}}]}
  :env {:http-port 8070})
