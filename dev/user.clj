(ns user
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [heroku-database-url-to-jdbc.core :refer [korma-connection-map]]
            [migratus.core :as migratus]
            reloaded.repl
            [specd.core :refer [app]]
            [system.components
             [jetty :refer [new-web-server]]
             [postgres :refer [new-postgres-database]]]))

(defn dev-system []
  (component/system-map
   :web (new-web-server (Integer. (env :port)) app)
   :db (new-postgres-database
        (assoc
         (korma-connection-map (env :database-url))
         :sslmode "require"))))

(reloaded.repl/set-init! #'dev-system)

;; clj configuration management
;; Tip for Emacsers!
;; (setq cider-refresh-before-fn "reloaded.repl/suspend" cider-refresh-after-fn "reloaded.repl/resume")

;; Running migrations
(def migratus-config
  {:store :database
   :migration-dir "migrations/"
   :db (korma-connection-map (env :database-url))})

;; (migratus/create migratus-config "create-routes")
;; (migratus/migrate migratus-config)
;; (migratus/rollback migratus-config)

;; (migratus/create config "create-user")

;; Tip for Emacsers!
;; (setq cider-refresh-before-fn "user/stop" cider-refresh-after-fn "user/start")
