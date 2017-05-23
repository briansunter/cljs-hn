# cljs-hn

A react-native HackerNews reader written in clojurescript.
Uses re-frame, reagent, and react-navigation

## Ideas:

 * Have one codebase work with both iOS and Android
 * Work well with existing react-native components
 * Interactive development and live-reloading
 * Represent views as data structures
 * Changes to state are pure functions that take actions
 * Declarative native routing on both platforms
 * Side effects strictly separated and easily mockable
 * Aggressive pre-caching and excellent offline experience
 * All apis documented and validated by clojure.spec

## Usage
[re-natal](https://github.com/drapanjanas/re-natal)

```
$ npm install -g re-natal
```

## Development with Figwheel

Initially the `index.*.js` files are generated with the production profile, ready for deployment.
However, during development it is better to use the development profile and integrate with Figwheel. Switch to the development profile with:

```
$ re-natal use-figwheel
```

This command needs to be run every time you switch to the development profile or specify a development environment (with `use-ios-device` or `use-android-device`).

NOTE: You might need to restart React Native Packager and reload your app.

Start the Figwheel REPL with

```
$ lein figwheel [ios | android]
```

If all went well you should see the REPL prompt and changes in source files should be hot-loaded by Figwheel.

#### Starting Figwheel REPL from nREPL
To start Figwheel from within nREPL session:
```
$ lein repl
```
Then in the nREPL prompt type:
```
user=> (start-figwheel "ios")
```
Or, for Android build type:
```
user=> (start-figwheel "android")
```
Or, for both type:
```
user=> (start-figwheel "ios" "android")
```

## Running the app
### Note for Linux users
On Linux, the React Native Packager has to be started manually with
```
react-native start
```
See [here](#running-on-linux) for more details.

### iOS
#### Using iOS simulator

```
re-natal use-ios-device simulator
react-native run-ios
```

#### Using real iOS device

```
re-natal use-ios-device real
```

If this doesn't correctly detect your computer's IP you can pass your IP address explicitly: `re-natal use-ios-device <IP address>`.
And then run

```
react-native run-ios
```

#### Switching between iOS devices
Run `use-ios-device` to configure device type you want to use in development:
```
$ re-natal use-ios-device <real|simulator>
$ re-natal use-figwheel
$ lein figwheel ios
```

---

### Android

#### Using Android Virtual Device (AVD)
[Set up a virtual device in AVD](https://developer.android.com/studio/run/managing-avds.html). Start the virtual device then run
```
$ re-natal use-android-device avd
$ re-natal use-figwheel
$ lein figwheel android
$ react-native run-android
```

#### Using Genymotion simulator
Set up and start the Genymotion simulator then run
```
$ re-natal use-android-device genymotion
$ re-natal use-figwheel
$ lein figwheel android
$ react-native run-android
```

#### Using real Android device
To run figwheel with real Android device please read [Running on Device](https://facebook.github.io/react-native/docs/running-on-device-android.html#content).
To make it work on a USB connected device I also had to run:
```
$ adb reverse tcp:8081 tcp:8081
$ adb reverse tcp:3449 tcp:3449
```
Then:
```
$ re-natal use-android-device real
$ re-natal use-figwheel
$ lein figwheel android
$ react-native run-android
```

#### Switching between Android devices
Run `use-android-device` to configure device type you want to use in development:
```
$ re-natal use-android-device <real|genymotion|avd>
$ re-natal use-figwheel
$ lein figwheel android
```

### Developing iOS and Android apps simultaneously
```
$ re-natal use-figwheel
$ lein figwheel ios android
```

---

### Using external React Native Components

Lets say you have installed an external library from npm like this:
```
$ npm i some-library --save
```

And you want to use a component called 'some-library/Component':
```clojure
(def Component (js/require "some-library/Component"))
```
This would work when you do `lein prod-build` and run your app, but will fail when you run with Figwheel.
The React Native packager statically scans for all calls to `require` and prepares the required
code to be available at runtime. But, dynamically loaded (by Figwheel) code bypasses this scan
and therefore requiring the custom component fails.

To overcome this run `use-component`:
```
$ re-natal use-component some-library/Component
```
or for a platform-specific component use the optional platform parameter:
```
$ re-natal use-component some-library/ComponentIOS ios
```
Then, regenerate index.*.js files:
```
$ re-natal use-figwheel
```
Lastly, you will have to restart the packager and reload your app.

NOTE: If you mistyped something, or no longer use the component and would like to remove it,
 manually open `.re-natal` and fix it there (it's just a list of names in JSON format, so the process should be straight forward).


## REPL
You have to reload your app, and should see the REPL coming up with the prompt.

At the REPL prompt, try loading your app's namespace:

```clojure
(in-ns 'future-app.ios.core)
```

Changes you make via the REPL or by changing your `.cljs` files should appear live
in the simulator.

Try this command as an example:

```clojure
(dispatch [:set-greeting "Hello Native World!"])
```

## Running on Linux
In addition to the instructions above on Linux you might need to
start React Native packager manually with command `react-native start`.
This was reported in [#3](https://github.com/drapanjanas/re-natal/issues/3)

See this [tutorial](https://gadfly361.github.io/gadfly-blog/2016-11-13-clean-install-of-ubuntu-to-re-natal.html) on how to set up and run re-natal on a clean install of Ubuntu.

See also [Linux and Windows support](https://facebook.github.io/react-native/docs/linux-windows-support.html)
in React Native docs.

## Support of UWP and WPF apps (using react-native-windows)

To start new project with UWP app:
```
$ re-natal init FutureApp -u
```

To start new project with WPF app:
```
$ re-natal init FutureApp -w
```

Existing projects can also add windows platforms any time using commands:
```
$ re-natal add-platform windows

or

$ re-natal add-platform wpf
```
Note: for projects generated with re-natal version prior to 0.4.0 additional windows builds will not be added automatically to `project.clj`.
Workaround is to generate fresh windows project and copy-paste additional builds manually.

## Production build
Do this with command:
```
$ lein prod-build
```
Follow the [React Native documentation](https://facebook.github.io/react-native/docs/signed-apk-android.html) to proceed with the release.

## Static Images
Since version 0.14 React Native supports a [unified way of referencing static images](https://facebook.github.io/react-native/docs/images.html)

In Re-Natal skeleton images are stored in "images" directory. Place your images there and reference them from cljs code:
```clojure
(def my-img (js/require "./images/my-img.png"))
```
#### Adding an image during development
When you have dropped a new image to "images" dir, you need to restart React Native packager and re-run command:
```
$ re-natal use-figwheel
```
This is needed to regenerate index.\*.js files which includes `require` calls to all local images.
After this you can use a new image in your cljs code.

## Upgrading existing Re-Natal project

#### Upgrading React Native version

To upgrade React Native to newer version please follow the official
[Upgrading](https://facebook.github.io/react-native/docs/upgrading.html) guide of React Native.
Re-Natal makes almost no changes to the files generated by react-native so the official guide should be valid.

#### Upgrading Re-Natal CLI version
Do this if you want to use newer version of re-natal.

Commit or backup your current project, so that you can restore it in case of any problem ;)

Upgrade re-natal npm package
```
$ npm upgrade -g re-natal
```
In root directory of your project run
```
$ re-natal upgrade
```
This will overwrite only some files which usually contain fixes in newer versions of re-natal,
and are unlikely to be changed by the user. No checks are done, these files are just overwritten:
  - files in /env directory
  - figwheel-bridge.js

Then to continue development using figwheel
```
$ re-natal use-figwheel
```

## Enabling source maps when debugging in chrome
To make source maps available in "Debug in Chrome" mode re-natal patches
the react native packager to serve \*.map files from file system and generate only index.\*.map file.
To achieve this [this line](https://github.com/facebook/react-native/blob/master/packager/react-packager/src/Server/index.js#L413)
of file "node_modules/react-native/packager/react-packager/src/Server/index.js" is modified to match only index.\*.map

To do this run: `re-natal enable-source-maps` and restart packager.

You can undo this any time by deleting `node_modules` and running `re-natal deps`



## License

Copyright © 2017 Brian Sunter

Distributed under the Eclipse Public License either version 1.0
