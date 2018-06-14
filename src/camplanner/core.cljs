(ns camplanner.core
    (:require [reagent.core :as r]))

(enable-console-print!)

(println "Test!")

;; define your app data so that it doesn't get over-written on reload

; (def app-state (atom {}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def korea_map [ '( 42.25 133 )
        '( 43, 132.5 )
        '( 42.5 132 )
        '( 42.25, 131 )
        '( 42.25, 130 )
        '( 41.5 130 )
        '( 41.5, 129)
        '( 41.75, 128)
        '( 41, 127)
        '( 40.75, 126)
        '( 40.5, 125.25)
        '( 40 124.5 )
        '( 39.5 126 )
        '( 39 126.25 )
        '( 38.25 125.25 )
        '( 37.75 126 )
        '( 37.75 127 )
        '( 37.5 127.5 )
        '( 37 127.75 )
        '( 37 127 )
        '( 36 126.5 )
        '( 35.25 127 )
        '( 34.5 127 )
        '( 34.5 128 )
        '( 34.75 129 )
        '( 35 130 )
        '( 35.25 130.5 )
        '( 36 131 )
        '( 37 131 )
        '( 38 130.25 )
        '( 38.75 130.75 )
        '( 39.25 128.75 )
        '( 39.75 129 )
        '( 40.5 131 )
        '( 41 132 )
        '( 41.75 132 ) ] )

(def air-bases [ [ "Uiju" 40.25 125 ]
             [ "Panghyon" 40 125.75]
             [ "Taechon" 40 126.25 ]
             [ "Kaechon" 39.75 126.75 ]
             [ "Pukchang-Up" 39.50 126.75 ]
             [ "Sunchon" 39.25 126.75 ]
             [ "Sunan" 39.25 126.50 ]
             [ "Mirim" 39 126.75 ]
             [ "Hwangju" 38.75 126.50 ]
             [ "Koksan" 38.75 127.5 ]
             [ "Hyon-Ni" 38.75 128.50 ]
             [ "Wonsan" 39.25 128.75 ]
             [ "Sondok" 39.75 128.75 ]
             [ "Kuum-Ni" 39 129.25 ]
             [ "Taetan" 38.25 125.75 ]
             [ "Ongjin" 38 126 ]
             [ "Haeju" 38 126.50 ]
             [ "Manpo" 41.25 127.50 ]
             [ "Toksan" 40 129 ]
             [ "Hwangsuwon" 40.75 129.75 ]
             [ "Iwon" 40.50 130.50 ]
             [ "Samjiyon-Up" 42 130.25 ]
             [ "Orang" 41.50 131.75 ] ])

(def korea_string
  (->> (map #(str (first %) "," (last %)) camplanner.core/korea_map)
       (interpose " ")
       (reduce str) )
  )

(defn toggle-installation! [atm typ nam]
  (swap! atm (fn [a] (assoc a typ
                            (if (contains? (typ a) nam)
                              (disj (typ a) nam)
                              (conj (typ a) nam)
                              ))))
  )

(defn installation-selected? [atm typ nam]
  (contains? (typ @atm) nam))

(defn airbases-component [selection text]
  (into [] (concat
    [:g {:id "airbases"}
     [:text (cljs.pprint/pprint selection)]]
    (map (fn [e] [:circle {:cx (second e) :cy (last e) :r 0.05 :stoke-width 0
                           :fill (if (installation-selected? selection :airbases (first e)) "red" "grey")
                           :on-mouse-move (fn [ev] (reset! text (first e)))
                           :on-mouse-leave (fn [ev] (reset! text ""))
                           :on-mouse-down (fn [ev] (toggle-installation! selection :airbases (first e))) }]
           ) air-bases)
    ))
  )

(defn the_map [visib text]
  (let [selection (r/atom { :airbases #{} })]
    (fn []
      [:svg {:viewBox "0 0 10 10"}
       [:g {:id "map" :transform "scale(1 1) translate(-43 -124) rotate(-90 43 124)" :x 0 :y 0}
        [:polygon {:fill "white" :stroke "black" :stroke-width 0.02
                   :points korea_string }]
        (when (:airbases @visib) [airbases-component selection text])
        ]
       ]))
  )

(defn swap-toggle! [s key]
  (swap! s (fn [s] (assoc s key (if (key s) nil 1))))
  )

(defn page_component []
  (let [app-state (r/atom {}) status-line-text (r/atom "")]
    (fn []
      [:div
       [:h1 "Korea Campaign Planner"]
       [:div#menu [:ul {:id "options"}
        [:li#airbases {:on-click (fn [] (swap-toggle! app-state :airbases)) :class (if (:airbases @app-state) "selected" "not-selected") } "Airbases" ]
        [:li#samsites {:on-click (fn [] (swap-toggle! app-state :sam-sites)) :class (if (:sam-sites @app-state) "selected" "not-selected") } "SAM Sites" ]
        [:li#armybases {:on-click (fn [] (swap-toggle! app-state :army-bases)) } "Army Bases"]
        ]
        [:p @status-line-text]
        ]
       [:div#the-map [the_map app-state status-line-text] ]
       ]
      )
    )
  )

(r/render [page_component] (js/document.getElementById "app"))
