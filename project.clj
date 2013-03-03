(defproject text-adventure-editor "0.1.0-SNAPSHOT"
            :description "A self-hosted web app editor for my text adventure game"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [monet "0.1.0-SNAPSHOT"]
                           [jayq "0.1.0-SNAPSHOT"]]

  					;; Add lein-cljsbuild plugin
            :plugins [[lein-cljsbuild "0.2.8"]]
            
            ;; config. for cljsbuild
            :cljsbuild {
                        :builds [{
                                  :source-path "src/textadventureeditor/client"
                                  :compiler {
                                             :output-to "resources/public/js/main.js"
                                             :optimizations :whitespace
                                             :pretty-print true}}]}
  					
  					:main textadventureeditor.server)

