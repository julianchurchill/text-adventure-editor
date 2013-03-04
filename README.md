text-adventure-editor
=====================

A self-hosted web app editor for my text adventure game.

Written in Clojure with Noir. Requires Leiningen for easy building and running as a Clojure project

Run 'lein run' to start the app and open http://localhost:8080 in a browser.

For development make sure you run 'lein cljsbuild auto' to regenerate the javascript when 
changes are made to any clojurescript code.

Useful links
============

- Interactive canvas' - See http://simonsarris.com/blog/510-making-html5-canvas-useful
- Getting started with ClojureScript - See http://samrat.me/blog/2012/10/getting-started-with-clojurescript/

todo
====

- [FEATURE] Save button (and enter key) to save edited id and description to location
- [FEATURE] Delete button to remove location from the model (plus confirm dialog)
- [FEATURE] Locations have id in the graphic for quick reference
- [FEATURE] Drag mouse from one location to another to add a two-way exit with defaults
- [FEATURE] Show all exits in a list with the location properties
- [FEATURE] Exits are drawn as a line with arrow heads from one location to another
- [FEATURE] Hidden exits are dotted lines
- [FEATURE] Exits list has save button by each exit to save edited properties
- [FEATURE] Exits list has delete button by each exit to delete from model
- [FEATURE] Locations have an items list
- [FEATURE] Item list has save button by each item to save edited properties
- [FEATURE] Item list has delete button by each item to delete from model
- [FEATURE] Export button to save model to text file in format that is readable by the text adventure game
- [FEATURE] Import button to load a model in a text file
- [FEATURE] Locations are draggable 
- [FEATURE] Loading a model from a text file lays out locations so they can all be seen

done
====

DONE - [FEATURE] Click adds new location
DONE - [FEATURE] Click a location to see editable properties, including name and description
