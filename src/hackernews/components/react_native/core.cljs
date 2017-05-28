(ns hackernews.components.react-native.core
  (:require [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def html-view (r/adapt-react-class (.-default (js/require "react-native-htmlview/HTMLView"))))
(def app-registry (.-AppRegistry ReactNative))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def linking (.-Linking ReactNative))
(def react-navigation (js/require "react-navigation"))
(def stack-navigator (.-StackNavigator react-navigation))
(def add-navigation-helpers (.-addNavigationHelpers react-navigation))
(def drawer-navigator (.-DrawerNavigator react-navigation))
