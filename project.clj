(defproject text-adventure-editor "0.1.0-SNAPSHOT"
  :description "A self-hosted web app editor for my text adventure game"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]
                 [crate "0.2.4"]
                 [monet "0.1.0-SNAPSHOT"]
                 [fetch "0.1.0-alpha2"]
                 [jayq "2.3.0"]
                 [textadventure.businesslogic "1.0.0-SNAPSHOT"]]

  ;; Add lein-cljsbuild plugin
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-localrepo "0.4.1"]]
            
  ;; config. for cljsbuild
  :cljsbuild {:builds
              [{:source-paths ["src/textadventureeditor/client"],
                :compiler
                {:output-to "resources/public/js/main.js",
                 :optimizations :simple,
                 ;need this if optimization == advanced
                 ; also need to include jquery.js in our source
                 ;:externs ["externs/jquery.js"],
                 :pretty-print true}}]}
  
  :main textadventureeditor.server)

