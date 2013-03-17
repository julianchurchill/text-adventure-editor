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

- [FEATURE] Items have 'add item action' button
[REFACTOR] - Factor out append of delete button from add-extra-fields-for-item 
 and from add-extra-fields-for-exit and put it in add-all-property-fields or add-property-fields
- [FEATURE] Items have 'delete' button for each item action
- [FEATURE] Export button to save model to text file in format that is readable by the text adventure game
- [FEATURE] Import button to load a model in a text file
- [FEATURE] Delete button to remove location from the model (plus confirm dialog)
PARTIAL - [FEATURE] Locations have id in the graphic for quick reference.
  - Font should be large, centred and a contrasting colour.
- [FEATURE] Exits are drawn as a line with arrow heads from one location to another
- [FEATURE] Loading a model from a text file lays out locations so they can all be seen - perhaps enforce a grid
- [FEATURE] Exit direction hint is restricted to N,S,E,W as a drop-down list
- [FEATURE] Exit destinations are restricted to the available location ids as a drop-down list
- [FEATURE] Order of fields is variable - make field maps ordered
- [FEATURE] Hidden exits are dotted lines
- [FEATURE] Enter key to save edits
- [FEATURE] Locations are draggable 
- [FEATURE] Make pretty!

done
====

DONE - [FEATURE] Item actions get saved when the save button is pressed
DONE - [FEATURE] Items have item actions list
DONE - [FEATURE] Item is untakeable and item use is not repeatable should be check boxes
DONE - [FEATURE] Item list has delete button by each item to delete from model
DONE - [FEATURE] Location save button saves items list
DONE - [FEATURE] Locations have an 'add item' button
DONE - [FEATURE] Add labels for location, item and exit fields
DONE - [FEATURE] Locations have an items list
DONE - [FEATURE] Exits list has delete button by each exit to delete from model
DONE - [FEATURE] Add exit button to create an exit
DONE - [FEATURE] Location save button saves edited exit properties
DONE - [FEATURE] Show all exits in a list with the location properties
DONE - [FEATURE] Save button to save edited id and description to location
DONE - [FEATURE] Click adds new location
DONE - [FEATURE] Click a location to see editable properties, including name and description
