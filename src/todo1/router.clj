(ns todo1.router)

(defn- split-uri [uri]
  "Splits uri by '/' character.
  It assumes that all valid path are starting with /,
  therefore it is safe to drop leading element of the split
  (it is empty)."
  (rest (clojure.string/split uri #"/")))

(defn- keyword-string? [s]
  (= \: (first s)))

(defn- parse-uri-pattern-part [s]
  "in uri-pattern keyword is like :keyword"
  (if (keyword-string? s)
    (keyword (clojure.string/join (rest s)))
    s))

(defn- parse-uri-pattern [s]
  (map parse-uri-pattern-part (split-uri s)))

(defn- match-part [uri-part pattern-part]
  "returns {:keyword value} if matches and pattern part is a keyword,
  true, if matches, false otherwise"
  (if (keyword? pattern-part)
    {pattern-part uri-part}
    (= pattern-part uri-part)))

(defn- match-pattern [uri pattern]
  "returns {:keyword value} map if matches, nil otherwise"
  (let [splitted-uri (split-uri uri)
        parsed-pattern (parse-uri-pattern pattern)
        matches (map match-part splitted-uri parsed-pattern)]
    (when (= (count splitted-uri) (count parsed-pattern))
      (into {}
            (filter map? matches)))))

(defn- match-request-method [method accepted-methods]
  (some #{method} accepted-methods))

(defn- match-request [request route-spec]
  (let [[pattern accepted-methods handler] route-spec
        {:keys [uri request-method]} request]
    (when (match-request-method request-method accepted-methods)
      [(match-pattern uri pattern) request-method handler])))

(defn route
  [default-handler & route-specs]
  (fn [request]
    (let [parsed (map #(match-request request %) route-specs)
          matching (first (filter first parsed))
          method (:request-method request)]
      (if matching
        (let [[route-params method handler] matching]
          (handler request method route-params))
        (default-handler request method {})))))
