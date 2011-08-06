(import '(javax.swing
          JFrame
          JPanel
          JButton
          JOptionPane
          JLabel))
(import '(java.awt.event ActionListener))

;;; Make a button
(def button (JButton. "Click Me!"))

;;; Container and Frames
(def panel (doto (JPanel.)
             (.add button)))

;;; Make a Window
(def frame (doto (JFrame. "Hello Frame")
             (.setSize 200 200)
             (.setContentPane panel)
             (.setVisible true)))

;; The revalidate method is not something you¡¯ll read about in most Swing
;; tutorials, because in pre-compiled Java it¡¯s rarely necessary.  Basically,
;; it tells Swing, ¡°I just changed the layout, you need to redraw stuff.¡±
;; Starting at our JButton, Swing searches up the containment hierarchy to the
;; top-level container, and redraws it.
;; (.revalidate button)

(defn say-hello []
  (JOptionPane/showMessageDialog nil "Hello, World!" "Greeting"
                                 JOptionPane/INFORMATION_MESSAGE))

(def act (proxy [ActionListener] []
           (actionPerformed [event] (say-hello))))
(.addActionListener button act)

(defn counter-app []
  (let [counter (atom 0)
        label (JLabel. "Counter: 0")
        button (doto (JButton. "Add 1")
                 (on-action evnt
                            (.setText label
                                      (str "Counter:" (swap! counter inc))))) 
        panel (doto (JPanel.)
                (.setOpaque true)
                (.add label)
                (.add button))]
    (doto (JFrame. "Counter App")
      (.setContentPane panel)
      (.setSize 300 100)
      (.setVisible true))))

(defmacro on-action [component event & body]
  `(.~component addActionListener
                (proxy [java.awt.event.ActionListener] []
                  (actionPerformed [~event] ~@body))))
