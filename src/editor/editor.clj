(import '(javax.swing
          JFrame
          JPanel
          JButton
          JOptionPane))
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