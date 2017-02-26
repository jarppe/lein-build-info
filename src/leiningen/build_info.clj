(ns leiningen.build-info
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh])
  (:import (java.time.format DateTimeFormatter)
           (java.time ZonedDateTime)))

(defn build-info [project & args]
  (let [build-info (or (-> project
                           :build-info
                           :output)
                       "resources/build-info.edn")
        info {:version (let [resp (sh/sh "git" "describe")]
                         (if (-> resp :exit zero?)
                           (-> resp :out)
                           "DEV"))
              :build-time (-> (ZonedDateTime/now)
                              (.format DateTimeFormatter/ISO_OFFSET_DATE_TIME))
              :build-by (System/getProperty "user.name")}]
    (with-open [out (-> build-info
                        (io/file)
                        (io/writer))]
      (.write out (pr-str info)))))
