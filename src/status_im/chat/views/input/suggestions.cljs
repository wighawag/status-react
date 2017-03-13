(ns status-im.chat.views.input.suggestions
  (:require-macros [status-im.utils.views :refer [defview]])
  (:require [re-frame.core :refer [subscribe dispatch]]
            [status-im.components.react :refer [view
                                                scroll-view
                                                touchable-highlight
                                                text
                                                icon]]
            [status-im.chat.styles.input.suggestions :as style]
            [status-im.i18n :refer [label]]
            [taoensso.timbre :as log]))

(defn suggestion-item-general [{:keys [on-press name description]}]
  [touchable-highlight {:on-press on-press}
   [view (style/item-suggestion-container true)
    [view {:style style/item-suggestion-name}
     [text {:style style/item-suggestion-name-text
            :font  :roboto-mono}
      "/" name]]
    [text {:style style/item-suggestion-description}
     description]]])

(defview request-item [index {:keys [type message-id]}]
  [{:keys [name description] :as response} [:get-response type]
   {:keys [chat-id]} [:get-current-chat]]
  [suggestion-item-general {:on-press    #(dispatch [:set-response-chat-command message-id type])
                            :name        name
                            :description description}])

(defview suggestion-item [index [command {:keys [title name description]}]]
  []
  [suggestion-item-general {:on-press    #(dispatch [:set-chat-command command])
                            :name        name
                            :description description}])

(defn item-title [top-padding? s]
  [view (style/item-title-container top-padding?)
   [text {:style style/item-title-text}
    s]])

(defn header []
  [view {:style style/header-container}
   [view style/header-icon]])

(defview suggestions-view []
  [input-height [:chat-ui-props :input-height]
   requests [:get-requests]
   suggestions [:get-suggestions]]
  [view (style/root 200 input-height)
   [header]
   [view {:flex 1}
    [scroll-view {:keyboardShouldPersistTaps true}
     (when (seq requests)
       [view
        [item-title false (label :t/suggestions-requests)]
        (for [{:keys [chat-id message-id] :as request} requests]
          ^{:key [chat-id message-id]}
          [request-item 0 request])])
     (when (seq suggestions)
       [view
        [item-title (seq requests) (label :t/suggestions-commands)]
        (for [suggestion (remove #(nil? (:title (second %))) suggestions)]
          ^{:key (first suggestion)}
          [suggestion-item 0 suggestion])])]]])