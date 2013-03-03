goog.provide('textadventureeditor.client.main');
goog.require('cljs.core');
goog.require('jayq.core');
goog.require('jayq.core');
goog.require('monet.geometry');
goog.require('monet.canvas');
textadventureeditor.client.main.$body = jayq.core.$.call(null,"\uFDD0'body");
textadventureeditor.client.main.canvas = monet.canvas.init.call(null,jayq.core.$.call(null,"\uFDD0'#canvas").get(0));
monet.canvas.add_entity.call(null,textadventureeditor.client.main.canvas,"\uFDD0'background",monet.canvas.entity.call(null,cljs.core.ObjMap.fromObject(["\uFDD0'x","\uFDD0'y","\uFDD0'w","\uFDD0'h"],{"\uFDD0'x":0,"\uFDD0'y":0,"\uFDD0'w":600,"\uFDD0'h":650}),null,(function (ctx,me){
return monet.canvas.rect.call(null,monet.canvas.fill_style.call(null,ctx,"#666"),me);
})));
alert("Hey there!");
