(defproject specd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.danielsz/system "0.3.1-SNAPSHOT"]
                 [com.stuartsierra/component "0.3.1"]
                 [reloaded.repl "0.2.1"]
                 [environ "1.0.0"]
                 [compojure "1.5.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [figwheel-sidecar "0.5.4-SNAPSHOT"]
                 [com.cemerick/piggieback "0.2.1"]
                 [ring "1.5.0"]
                 [hiccup "1.0.5"]
                 [heroku-database-url-to-jdbc "0.2.2"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [postgresql "9.3-1102.jdbc41"]
                 [korma "0.4.2"]
                 [migratus "0.8.9"]]
  :main ^:skip-aot specd.core
  :min-lein-version "2.0.0"
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :target-path "target/%s"
  :hooks [environ.leiningen.hooks]
  :profiles {:dev {:source-paths ["dev"]}
             :uberjar {:aot :all}
             :production {:env {:production true}}}
  :plugins [[lein-cljsbuild "1.1.3"]
            [migratus-lein "0.1.7"]
            [lein-figwheel "0.5.4-SNAPSHOT"]
            [environ/environ.lein "0.3.1"]]
  :figwheel {:css-dirs ["resources/public/css"]}
  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src" "dev"]
                :asset-path "out"
                :compiler {:optimizations :none
                           :output-to "dev-resources/public/main.js"
                           :pretty-print false}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:optimizations :advanced
                           :output-to "resources/public/main.js"
                           :pretty-print false}}]}
  :uberjar-name "specd-standalone.jar"
  :env {:http-port 8070
        :database-url "postgres://postgres@127.0.0.1:5432/specd"
        :password-salt "asdalskdjadjqow"}
  :migratus {:store :database
             :migration-dir "migrations"
             :db ~(get (System/getenv) "DATABASE_URL")})
