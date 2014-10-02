(ns hikari-cp.core-test
  (:require [hikari-cp.core :refer :all]
            [schema.core :as s])
  (:use expectations))

(def valid-options
  {:auto-commit        false
   :read-only          true
   :connection-timeout 100
   :idle-timeout       0
   :max-lifetime       0
   :minimum-idle       0
   :maximum-pool-size  0
   :adapter            :postgresql
   :username           "username"
   :password           "password"
   :database-name      "database"
   :server-name        "host-1"
   :port-number        5433})

(def datasource-config-with-required-settings
  (datasource-config (apply dissoc valid-options (keys default-datasource-options))))

(def datasource-config-with-overrides
  (datasource-config valid-options))

(expect true
        (.isAutoCommit datasource-config-with-required-settings))
(expect false
        (.isReadOnly datasource-config-with-required-settings))
(expect 30000
        (.getConnectionTimeout datasource-config-with-required-settings))
(expect 600000
        (.getIdleTimeout datasource-config-with-required-settings))
(expect 1800000
        (.getMaxLifetime datasource-config-with-required-settings))
(expect 10
        (.getMinimumIdle datasource-config-with-required-settings))
(expect 10
        (.getMaximumPoolSize datasource-config-with-required-settings))
(expect "org.postgresql.ds.PGSimpleDataSource"
        (.getDataSourceClassName datasource-config-with-required-settings))
(expect "username"
        (.getUsername datasource-config-with-required-settings))
(expect "password"
        (.getPassword datasource-config-with-required-settings))
; Quick and dirty hack to read portNumber from Properties
(expect "{portNumber=5433, databaseName=database, serverName=host-1}"
        (str (.getDataSourceProperties datasource-config-with-required-settings)))

(expect false
        (.isAutoCommit datasource-config-with-overrides))
(expect true
        (.isReadOnly datasource-config-with-overrides))
(expect 100
        (.getConnectionTimeout datasource-config-with-overrides))
(expect 0
        (.getIdleTimeout datasource-config-with-overrides))
(expect 0
        (.getMaxLifetime datasource-config-with-overrides))
(expect 0
        (.getMinimumIdle datasource-config-with-overrides))
(expect 0
        (.getMaximumPoolSize datasource-config-with-overrides))

(expect IllegalArgumentException
        (datasource-config (dissoc valid-options :adapter)))
(expect "Invalid configuration options: (:adapter)"
        (try
          (datasource-config (dissoc valid-options :adapter))
          (catch IllegalArgumentException e
            (str (.getMessage e)))))

(expect map?
        (s/validate ConfigurationOptions valid-options))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:auto-commit 1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:read-only 1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:connection-timeout "foo"})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:connection-timeout 99})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:idle-timeout -1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:max-lifetime -1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:minimum-idle -1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:maximum-pool-size -1})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:adapter :foo})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:username nil})))
(expect map?
        (s/validate ConfigurationOptions (dissoc valid-options :username)))
(expect map?
        (s/validate ConfigurationOptions (dissoc valid-options :password)))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:password nil})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:database-name nil})))
(expect map?
        (s/validate ConfigurationOptions (dissoc valid-options :database-name)))
(expect map?
        (s/validate ConfigurationOptions (dissoc valid-options :server-name)))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:server-name nil})))
(expect clojure.lang.ExceptionInfo
        (s/validate ConfigurationOptions (merge valid-options {:port-number -1})))
(expect map?
        (s/validate ConfigurationOptions (dissoc valid-options :port-number)))
