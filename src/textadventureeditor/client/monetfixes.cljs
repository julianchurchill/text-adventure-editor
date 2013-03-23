(ns textadventureeditor.client.monetfixes)

; Takes a style string - e.g. "24px sans-serif"
(defn font-style-that-works [ctx style-string]
  (set! (.-font ctx) style-string)
  ctx)

; This is used for font colour as well as drawing fill colour
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
