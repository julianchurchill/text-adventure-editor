(ns textadventureeditor.client.monetfixes)

(defn font-style-that-works [ctx color]
  (set! (.-font ctx) color)
  ctx)

(defn fill-style-that-works [ctx color]
  (set! (.-fillStyle ctx) color)
  ctx)

(defn stroke-style-that-works [ctx color]
  (set! (.-strokeStyle ctx) color)
  ctx)

(defn stroke-width-that-works [ctx color]
  (set! (.-lineWidth ctx) color)
  ctx)

(defn alpha-that-works [ctx color]
  (set! (.-globalAlpha ctx) color)
  ctx)
