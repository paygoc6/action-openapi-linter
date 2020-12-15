(ns test
  (:require
   [clojure.test :as t :refer [deftest is testing]]
   [core :refer [log->plog]]))


(def with-gh-env-info
  "2020-12-14T13:47:43,881Z INFO - repo: paygoc6/mario-pipeline - ref: refs/heads/temp - workflow: TEMP - ns: mario.core - line: 109 - message: creating if not exists dir on: ./target")

(def with-gh-env-severe
  "2020-12-14T13:47:43,993Z SEVERE - repo: paygoc6/mario-pipeline - ref: refs/heads/temp - workflow: TEMP - ns: mario.spectral - line: 49 - message: Error on linting: [{:code \"oas3-hosts-https-only\", :linter-message \"Servers MUST be https and no other protocol is allowed.\", :severity 0, :start-line 19, :start-character 13, :end-line 19, :end-character 45}]")

(def without-gh-env-info
  "2020-12-15T18:47:07,378Z INFO - ns: mario.core - line: 109 - message: creating if not exists dir on: ./target")

(def without-gh-env-severe
  "2020-12-15T18:47:07,494Z SEVERE - ns: mario.spectral - line: 49 - message: Error on linting: [{:code \"oas3-hosts-https-only\", :linter-message \"Servers MUST be https and no other protocol is allowed.\", :severity 0, :start-line 19, :start-character 13, :end-line 19, :end-character 45}]")

(deftest log->plog-test
  (testing "Without github env vars"
    (is (= [{:index {:_index "mario-pipeline-2020-12"}}
            {:field {:info      "INFO"
                     :timestamp 1607953663881
                     :repo      "paygoc6/mario-pipeline"
                     :ref       "refs/heads/temp"
                     :workflow  "TEMP"
                     :ns        "mario.core"
                     :line      "109"
                     :message   "creating if not exists dir on: ./target"}}]
           (log->plog with-gh-env-info)))
    (is (= [{:index {:_index "mario-pipeline-2020-12"}}
            {:field {:info      "SEVERE"
                     :timestamp 1607953663993
                     :repo      "paygoc6/mario-pipeline"
                     :ref       "refs/heads/temp"
                     :workflow  "TEMP"
                     :ns        "mario.spectral"
                     :line      "49"
                     :message   "Error on linting: [{:code \"oas3-hosts-https-only\", :linter-message \"Servers MUST be https and no other protocol is allowed.\", :severity 0, :start-line 19, :start-character 13, :end-line 19, :end-character 45}]"}}]
           (log->plog with-gh-env-severe))))
  (testing "With github env vars"
    (is (= [{:index {:_index "mario-pipeline-2020-12"}}
            {:field {:info      "INFO"
                     :timestamp 1608058027378
                     :ns        "mario.core"
                     :line      "109"
                     :message   "creating if not exists dir on: ./target"}}]
           (log->plog without-gh-env-info)))
    (is (= [{:index {:_index "mario-pipeline-2020-12"}}
            {:field {:info      "SEVERE"
                     :timestamp 1608058027494
                     :ns        "mario.spectral"
                     :line      "49"
                     :message   "Error on linting: [{:code \"oas3-hosts-https-only\", :linter-message \"Servers MUST be https and no other protocol is allowed.\", :severity 0, :start-line 19, :start-character 13, :end-line 19, :end-character 45}]"}}]
           (log->plog without-gh-env-severe)))))

(defn -main [& _args]
  (let [{:keys [:fail :error]} (t/run-tests 'test)]
    (System/exit (+ fail error))))
