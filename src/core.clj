(ns core
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(defn dtz-comma->timestamp [dtz-comma]
  (-> dtz-comma
      (str/replace #"," ".")
      ((get default-data-readers 'inst))
      .getTime))

(defn rest-msg->log [rest-msg]
  (->> rest-msg
       (map #(str/split % #" "))
       (map (fn [[k v & rest]]
              [(keyword (str/replace k #":" ""))
               (str/join #" " (conj rest v))]))
       (map (fn [[k v]]
              (if (nil? v)
                [:ns (name k)]
                [k v])))
       (into {})))

(defn log->plog [log]
  (let [[dtz-comma-info & rest-msg] (str/split log #" - ")
        [dtz-comma info]            (str/split dtz-comma-info #" ")]
    [{:index
      {:_index
       (str "mario-pipeline-"
            (.format (java.time.LocalDateTime/now)
                     (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM")))}}
     {:field
      (merge
       {:timestamp (dtz-comma->timestamp dtz-comma)
        :info      info}
       (rest-msg->log rest-msg))}]))

(defn log->treated-plog [raw-log]
  (try (log->plog raw-log)
       (catch Throwable _
         {:raw raw-log})))

(defn -main [& _args]
  (let [xf (comp
            (map slurp)
            (mapcat str/split-lines)
            (mapcat log->treated-plog)
            (map json/encode)
            (interpose "\n"))]
    (apply str (sequence xf *command-line-args*))))
