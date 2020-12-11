#!/usr/bin/env bb
(require '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[cheshire.core :as json])

(let [[log-file]   *command-line-args*
      current-time (java.util.Date.)]
  (->> (-> log-file
           slurp
           str/split-lines)
       (mapv (fn [raw]
               (try
                 (let [[dtz-comma-info & rest-msg] (str/split raw #" - ")
                       [dtz-comma info]            (str/split dtz-comma-info #" ")]
                   {:field
                    (merge
                     {:timestamp (-> dtz-comma
                                     (str/replace #"," ".")
                                     ((get default-data-readers 'inst))
                                     .getTime)
                      :info      info}
                     (->> rest-msg
                          (map #(str/split % #" "))
                          (map (fn [[k v & rest]]
                                 [(keyword (str/replace k #":" ""))
                                  (str/join #" " (conj rest v))]))
                          (map (fn [[k v]]
                                 (if (nil? v)
                                   [:ns (name k)]
                                   [k v])))
                          (into {})))})
                 (catch Throwable _
                   {:raw raw}))))
       (mapcat (fn [v]
                 [{:index {:_index (str "mario-pipeline-" (.format (java.time.LocalDateTime/now) (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM")))}} v]))
       (mapv json/encode)
       (str/join "\n")))
