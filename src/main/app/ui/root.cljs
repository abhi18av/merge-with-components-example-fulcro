(ns app.ui.root
  (:require
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [app.model.session :as session]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as prim :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [clojure.string :as str]))

;; ;;;;;;;;;;;;;;;;
;; ;; Utils
;; ;;;;;;;;;;;;;;;;

;; (defn field [{:keys [label valid? error-message] :as props}]
;;   (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
;;     (div :.ui.field
;;       (dom/label {:htmlFor label} label)
;;       (dom/input input-props)
;;       (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
;;         error-message))))

;; ;;;;;;;;;;;;;;;;
;; ;; SignUp
;; ;;;;;;;;;;;;;;;;

;; (defsc SignupSuccess [this props]
;;   {:query         ['*]
;;    :initial-state {}
;;    :ident         (fn [] [:component/id :signup-success])
;;    :route-segment ["signup-success"]
;;    :will-enter    (fn [app _] (dr/route-immediate [:component/id :signup-success]))}
;;   (div
;;     (dom/h3 "Signup Complete!")
;;     (dom/p "You can now log in!")))

;; (defsc Signup [this {:account/keys [email password password-again] :as props}]
;;   {:query             [:account/email :account/password :account/password-again fs/form-config-join]
;;    :initial-state     (fn [_]
;;                         (fs/add-form-config Signup
;;                           {:account/email          ""
;;                            :account/password       ""
;;                            :account/password-again ""}))
;;    :form-fields       #{:account/email :account/password :account/password-again}
;;    :ident             (fn [] session/signup-ident)
;;    :route-segment     ["signup"]
;;    :componentDidMount (fn [this]
;;                         (comp/transact! this [(session/clear-signup-form)]))
;;    :will-enter        (fn [app _] (dr/route-immediate [:component/id :signup]))}
;;   (let [submit!  (fn [evt]
;;                    (when (or (identical? true evt) (evt/enter-key? evt))
;;                      (comp/transact! this [(session/signup! {:email email :password password})])
;;                      (log/info "Sign up")))
;;         checked? (log/spy :info (fs/checked? props))]
;;     (div
;;       (dom/h3 "Signup")
;;       (div :.ui.form {:classes [(when checked? "error")]}
;;         (field {:label         "Email"
;;                 :value         (or email "")
;;                 :valid?        (session/valid-email? email)
;;                 :error-message "Must be an email address"
;;                 :autoComplete  "off"
;;                 :onKeyDown     submit!
;;                 :onChange      #(m/set-string! this :account/email :event %)})
;;         (field {:label         "Password"
;;                 :type          "password"
;;                 :value         (or password "")
;;                 :valid?        (session/valid-password? password)
;;                 :error-message "Password must be at least 8 characters."
;;                 :onKeyDown     submit!
;;                 :autoComplete  "off"
;;                 :onChange      #(m/set-string! this :account/password :event %)})
;;         (field {:label         "Repeat Password" :type "password" :value (or password-again "")
;;                 :autoComplete  "off"
;;                 :valid?        (= password password-again)
;;                 :error-message "Passwords do not match."
;;                 :onChange      #(m/set-string! this :account/password-again :event %)})
;;         (dom/button :.ui.primary.button {:onClick #(submit! true)}
;;           "Sign Up")))))

;; (declare Session)

;; ;;;;;;;;;;;;;;;;
;; ;; LogIn
;; ;;;;;;;;;;;;;;;;

;; (defsc Login [this {:account/keys [email]
;;                     :ui/keys      [error open?] :as props}]
;;   {:query         [:ui/open? :ui/error :account/email
;;                    {[:component/id :session] (comp/get-query Session)}
;;                    [::uism/asm-id ::session/session]]
;;    :css           [[:.floating-menu {:position "absolute !important"
;;                                      :z-index  1000
;;                                      :width    "300px"
;;                                      :right    "0px"
;;                                      :top      "50px"}]]
;;    :initial-state {:account/email "" :ui/error ""}
;;    :ident         (fn [] [:component/id :login])}
;;   (let [current-state (uism/get-active-state this ::session/session)
;;         {current-user :account/name} (get props [:component/id :session])
;;         initial?      (= :initial current-state)
;;         loading?      (= :state/checking-session current-state)
;;         logged-in?    (= :state/logged-in current-state)
;;         {:keys [floating-menu]} (css/get-classnames Login)
;;         password      (or (comp/get-state this :password) "")] ; c.l. state for security
;;     (dom/div
;;       (when-not initial?
;;         (dom/div :.right.menu
;;           (if logged-in?
;;             (dom/button :.item
;;               {:onClick #(uism/trigger! this ::session/session :event/logout)}
;;               (dom/span current-user) ent/nbsp "Log out")
;;             (dom/div :.item {:style   {:position "relative"}
;;                              :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
;;               "Login"
;;               (when open?
;;                 (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
;;                                                                         ;; Stop bubbling (would trigger the menu toggle)
;;                                                                         (evt/stop-propagation! e))
;;                                                              :classes [floating-menu]}
;;                   (dom/h3 :.ui.header "Login")
;;                   (div :.ui.form {:classes [(when (seq error) "error")]}
;;                     (field {:label    "Email"
;;                             :value    email
;;                             :onChange #(m/set-string! this :account/email :event %)})
;;                     (field {:label    "Password"
;;                             :type     "password"
;;                             :value    password
;;                             :onChange #(comp/set-state! this {:password (evt/target-value %)})})
;;                     (div :.ui.error.message error)
;;                     (div :.ui.field
;;                       (dom/button :.ui.button
;;                         {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
;;                                                                                              :password password}))
;;                          :classes [(when loading? "loading")]} "Login"))
;;                     (div :.ui.message
;;                       (dom/p "Don't have an account?")
;;                       (dom/a {:onClick (fn []
;;                                          (uism/trigger! this ::session/session :event/toggle-modal {})
;;                                          (dr/change-route this ["signup"]))}
;;                         "Please sign up!"))))))))))))

;; (def ui-login (comp/factory Login))

;; ;;;;;;;;;;;;;;;;
;; ;; Main
;; ;;;;;;;;;;;;;;;;


;; (defsc Main [this props]
;;   {:query         [:main/welcome-message]
;;    :initial-state {:main/welcome-message "Hi!"}
;;    :ident         (fn [] [:component/id :main])
;;    :route-segment ["main"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :main]))}
;;   (div :.ui.container.segment
;;     (h3 "Main")))




;; ;;;;;;;;;;;;;;;;
;; ;; Settings
;; ;;;;;;;;;;;;;;;;

;; (defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
;;   {:query         [:account/time-zone :account/real-name]
;;    :ident         (fn [] [:component/id :settings])
;;    :route-segment ["settings"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :settings]))
;;    :initial-state {}}
;;   (div :.ui.container.segment
;;     (h3 "Settings")))

;; (dr/defrouter TopRouter [this props]
;;   {:router-targets [Main Signup SignupSuccess Settings]})

;; (def ui-top-router (comp/factory TopRouter))

;; ;;;;;;;;;;;;;;;;
;; ;; Session
;; ;;;;;;;;;;;;;;;;

;; (defsc Session
;;   "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
;;   [this {:keys [:session/valid? :account/name] :as props}]
;;   {:query         [:session/valid? :account/name]
;;    :ident         (fn [] [:component/id :session])
;;    :pre-merge     (fn [{:keys [data-tree]}]
;;                     (merge {:session/valid? false :account/name ""}
;;                       data-tree))
;;    :initial-state {:session/valid? false :account/name ""}})

;; (def ui-session (prim/factory Session))

;; ;;;;;;;;;;;;;;;;
;; ;; TopChrome
;; ;;;;;;;;;;;;;;;;

;; (defsc TopChrome [this {:root/keys [router current-session login]}]
;;   {:query         [{:root/router (comp/get-query TopRouter)}
;;                    {:root/current-session (comp/get-query Session)}
;;                    [::uism/asm-id ::TopRouter]
;;                    {:root/login (comp/get-query Login)}]
;;    :ident         (fn [] [:component/id :top-chrome])
;;    :initial-state {:root/router          {}
;;                    :root/login           {}
;;                    :root/current-session {}}}
;;   (let [current-tab (some-> (dr/current-route this this) first keyword)]
;;     (div :.ui.container
;;       (div :.ui.secondary.pointing.menu
;;         (dom/a :.item {:classes [(when (= :main current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["main"]))} "Main")
;;         (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
;;         (div :.right.menu
;;           (ui-login login)))
;;       (div :.ui.grid
;;         (div :.ui.row
;;           (ui-top-router router))))))

;; (def ui-top-chrome (comp/factory TopChrome))

;; ;;;;;;;;;;;;;;;;
;; ;; Bump Number
;; ;;;;;;;;;;;;;;;;

;; (defmutation bump-number [ignored]
;;   (action [{:keys [state]}]
;;           (swap! state update :root/number inc)))

;; (defsc BumpNumber [this {:root/keys [number]}]
;;        {:query         [:root/number]
;;         :initial-state {:root/number 0}}
;;        (dom/div
;;          (dom/h4 "This is an example.")
;;          (dom/button {:onClick #(comp/transact! this `[(bump-number {})])}
;;                      "You've clicked this button " number " times.")))

;; (def ui-bump-number (comp/factory BumpNumber))

;; ;;;;;;;;;;;;;;;;
;; ;; ROOT
;; ;;;;;;;;;;;;;;;;


;; (defsc Root [this {:root/keys [top-chrome]}]
;;   {:query             [{:root/top-chrome (comp/get-query TopChrome)}]
;;    :ident             (fn [] [:component/id :ROOT])
;;    :initial-state     {:root/top-chrome {}}}
;;   (div
;;     (ui-top-chrome top-chrome)
;;     (ui-bump-number {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; VANILLA EXAMPLES
;;;;;;;;;;;;;;;;






(defsc Counter [this {:keys [counter/id counter/n] :as props} {:keys [onClick] :as computed}]
       {:query [:counter/id :counter/n]
        :ident [:counter/by-id :counter/id]}
       (dom/div :.counter
                (dom/span :.counter-label
                          (str "Current count for counter " id ":  "))
                (dom/span :.counter-value n)
                (dom/button {:onClick #(onClick id)} "Increment")))

(def ui-counter (comp/factory Counter {:keyfn :counter/id}))

; the * suffix is just a notation to indicate an implementation of something..in this case the guts of a mutation
(defn increment-counter*
      "Increment a counter with ID counter-id in a Fulcro database."
      [database counter-id]
      (update-in database [:counter/by-id counter-id :counter/n] inc))

(defmutation increment-counter [{:keys [id] :as params}]
             ; The local thing to do
             (action [{:keys [state] :as env}]
                     (swap! state increment-counter* id))
             ; The remote thing to do. True means "the same (abstract) thing". False (or omitting it) means "nothing"
             (remote [env] true))

(defsc CounterPanel [this {:keys [counters]}]
       {:initial-state (fn [params] {:counters []})
        :query         [{:counters (comp/get-query Counter)}]
        :ident         (fn [] [:panels/by-kw :counter])}
       (let [click-callback (fn [id] (comp/transact! this
                                                     `[(increment-counter {:id ~id}) :counter/by-id]))]
            (dom/div
              ; embedded style: kind of silly in a real app, but doable
              (dom/style ".counter { width: 400px; padding-bottom: 20px; }
                  button { margin-left: 10px; }")
              ; computed lets us pass calculated data to our component's 3rd argument. It has to be
              ; combined into a single argument or the factory would not be React-compatible (not would it be able to handle
              ; children).
              (map #(ui-counter (comp/computed % {:onClick click-callback})) counters))))

(def ui-counter-panel (comp/factory CounterPanel))

(defonce timer-id (atom 0))
(declare sample-of-counter-app-with-merge-component-fulcro-app)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The code of interest...
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn add-counter
      "NOTE: A function callable from anywhere as long as you have a reconciler..."
      [app counter]
      (merge/merge-component! app Counter counter
                              :append [:panels/by-kw :counter :counters]))

(defsc Root [this {:keys [panel]}]
       {:query         [{:panel (comp/get-query CounterPanel)}]
        :initial-state {:panel {}}}
       (dom/div {:style {:border "1px solid black"}}
                ; NOTE: A plain function...pretend this is happening outside of the UI...we're doing it here so we can embed it in the book...
                (dom/button {:onClick #(add-counter (comp/any->app this) {:counter/id 4 :counter/n 22})} "Simulate Data Import")
                (dom/hr)
                "Counters:"
                (ui-counter-panel panel)))
