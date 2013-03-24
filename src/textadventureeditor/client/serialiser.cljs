(ns textadventureeditor.client.serialiser
  (:use [clojure.string :only [blank?]]))

(defn serialise-collection [coll member-serialise-func]
  (apply str (doall (map #(member-serialise-func %) coll))))

(defn serialise-item-action [item-action]
  (str "item use action:" (:action item-action) ":" (:param item-action) "\n"))

(defn serialise-item-actions [item-actions]
  (serialise-collection item-actions serialise-item-action))

(defn serialise-item [item]
  (str "ITEM\n"
       "item name:" (:name item) "\n"
       "item description:" (:description item) "\n"
       "item id:" (:id item) "\n"
       "item countable noun prefix:" (:countable-noun-prefix item) "\n"
       (if (not (blank? (:mid-sentence-cased-name item)))
         (str "item mid sentence cased name:" (:mid-sentence-cased-name item) "\n"))
       (if (:is-untakeable item)
         (str "item is untakeable:\n"))
       (if (not (blank? (:can-be-used-with item)))
         (str "item can be used with:" (:can-be-used-with item) "\n"))
       (if (not (blank? (:successful-use-message item)))
         (str "item successful use message:" (:successful-use-message item) "\n"))
       (if (:use-is-not-repeatable item)
         (str "item use is not repeatable:\n"))
       (serialise-item-actions (:use-actions item))
  ))

(defn serialise-items [items]
  (serialise-collection items serialise-item))

(defn serialise-exit [exit]
  (str "EXIT\n"
       "exit label:" (:label exit) "\n"
       "exit destination:" (:destination exit) "\n"
       "exit direction hint:" (:direction-hint exit) "\n"
       (if (:is-not-visible exit)
         (str "exit is not visible:\n"))
       "exit id:" (:id exit) "\n"))

(defn serialise-exits [exits]
  (serialise-collection exits serialise-exit))

(defn serialise-location [location]
  (str "LOCATION\n"
       "x:" (:x location) "\n"
       "y:" (:y location) "\n"
       "location id:" (:id location) "\n"
       "location description:" (clojure.string/trim-newline (:description location)) "\n"
       (serialise-exits (:exits location))
       (serialise-items (:items location))))

(defn serialise-locations [locations]
  (serialise-collection locations serialise-location))
