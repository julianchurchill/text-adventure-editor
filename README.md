text-adventure-editor
=====================

A self-hosted web app editor for my text adventure game.

Written in Clojure with Noir. Requires Leiningen for easy building and running as a Clojure project

Run 'lein run' to start the app and open http://localhost:8080 in a browser.

For development make sure you run 'lein cljsbuild auto' to regenerate the javascript when 
changes are made to any clojurescript code.


todo
====

- Investigate interactive canvas' - See http://simonsarris.com/blog/510-making-html5-canvas-useful
- Investigate ClojureScript - See http://samrat.me/blog/2012/10/getting-started-with-clojurescript/

- [FEATURE] Click adds new location
- [FEATURE] Click a location to see editable properties, including name and description
- [FEATURE] Save button to save model to text file in format that is readable by the text adventure game
- [FEATURE] Load button to load a model in a text file
- [FEATURE] Locations are draggable 
- [FEATURE] Loading a model from a text file lays out locations so they can all be seen
- [FEATURE] Locations are connectable by a two-way arrow to represent exits. Arrow ends can be labelled.
