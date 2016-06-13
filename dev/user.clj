(ns user
  (:require
   [com.stuartsierra.component :as component]
   [system.components.http-kit :refer [new-web-server]]
   [environ.core :refer [env]]
   [specd.core :refer [app]]
   reloaded.repl))

(defn dev-system []
  (component/system-map
   :web (new-web-server (Integer. (env :http-port)) app)))

(reloaded.repl/set-init! #'dev-system)

(defn start-system []
  (reloaded.repl/resume))

(defn stop-system []
  (reloaded.repl/suspend))

;; Tip for Emacsers!
;; (setq cider-refresh-before-fn "user/stop" cider-refresh-after-fn "user/start")

;; (setq cider-cljs-lein-repl
;;    "(do (require 'figwheel-sidecar.repl-api) (figwheel-sidecar.repl-api/start-figwheel!) (figwheel-sidecar.repl-api/cljs-repl))")

;; (defn start-cljs []
;;   (ra/start-figwheel!
;;     {:figwheel-options {:css-dirs ["resources/public/css"]}
;;      :build-ids ["dev"]   ;; <-- a vector of build ids to start autobuilding
;;      :all-builds          ;; <-- supply your build configs here
;;      [{:id "dev"
;;        :figwheel true
;;        :source-paths ["src"]
;;        :compiler {:main "client.web"
;;                   :asset-path "out"
;;                   :output-to "dev-resources/public/main.js"
;;                   :output-dir "dev-resources/public/out"
;;                   :verbose true}}]}))

;; (defn stop-cljs []
;;   (ra/stop-figwheel!))

;; (defn repl-cljs []
;;   (ra/cljs-repl))
