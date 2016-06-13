(ns specd.utils
  (:import
    java.security.SecureRandom
    javax.crypto.SecretKeyFactory
    javax.crypto.spec.PBEKeySpec))

;; This atom defined to support easiest reloading repl-flow
(def all-the-sessions (atom {}))

(defn pbkdf2
  ; Get a hash for the given string and optional salt
  ([x salt]
   (let [k (PBEKeySpec. (.toCharArray x) (.getBytes salt) 1000 192)
         f (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1")]
     (format "%x"
             (java.math.BigInteger. (.getEncoded (.generateSecret f k)))))))
