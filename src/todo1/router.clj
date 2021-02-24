(ns todo1.router
  (:require [clojure.string :as string]
            [ring.middleware.resource :as resource]))

(defn- split-uri [uri]
  "Splits uri on the first '/' character."
  (when-not (nil? uri)
    (clojure.string/split uri #"/" 2)))

(defn- keyword-string? [s]
  (when-not (nil? s)
    (string/starts-with? s ":")))

(defn- wildcard? [s]
  (= "*" s))

(defn- str-to-keyword [s]
  "Convert :x string to :x keyword"
  (keyword (string/join (rest s))))

(defn- parse-uri-pattern-part [s]
  "in uri-pattern keyword is like :keyword"
  (if (keyword-string? s)
    (keyword (clojure.string/join (rest s)))
    s))

(defn- remove-forward-slash [s]
  (if (string/starts-with? s "/")
    (subs s 1)
    s))

(defn- parse-uri-pattern [s]
  (map parse-uri-pattern-part (split-uri s)))

(defn- match-part [uri-part pattern-part]
  "returns {:keyword value} if matches and pattern part is a keyword,
  {}, if matches, nil otherwise"
  (if (keyword-string? pattern-part)
    {(str-to-keyword pattern-part) uri-part}
    (when (= pattern-part uri-part) {})))

(defn- match-pattern [uri pattern]
  (loop [uri uri
         pattern pattern
         accum {}]
    (if (wildcard? pattern)
      {:uri-params accum :uri-remaining uri}
      (let [[uri-part uri-remaining] (split-uri uri)
                 [pattern-part pattern-remaining] (split-uri pattern)
                 match (match-part uri-part pattern-part)
                 matches (into accum match)]
        (when match
          (if (and (empty? uri-remaining) (empty? pattern-remaining))
            {:uri-params matches
             :uri-remaining nil}
            (recur uri-remaining pattern-remaining
                   matches)))))))

(defn- match-request-method [method accepted-methods]
  (some #{method} accepted-methods))

(defn- update-request [request new-info]
  (assoc request
         :uri-params (merge (:uri-params request) (:uri-params new-info))
         :uri-remaining (:uri-remaining new-info)))

(defn- match-request [request route-spec]
  (let [[pattern accepted-methods handler] route-spec
        {:keys [uri-remaining request-method]} request
        pattern (remove-forward-slash pattern)
        method-match (match-request-method request-method accepted-methods)
        pattern-match (match-pattern uri-remaining pattern)]
    (when (and method-match pattern-match)
      [(update-request request pattern-match) handler])))

(defn- request-remove-forward-slash [request]
  (update request :uri-remaining remove-forward-slash))

(defn- prepare-request [request]
  (request-remove-forward-slash
   (merge {:uri-params nil :uri-remaining (:uri request)} request)))

(defn route
  [default-handler & route-specs]
  (fn [request]
    (let [request (prepare-request request)
          parsed (map #(match-request request %) route-specs)
          [request-with-match handler] (first (filter some? parsed))]
      (if request-with-match
        (handler request-with-match)
        (default-handler request)))))

(defn route-static [pub-path default-handler]
  (resource/wrap-resource default-handler pub-path))
