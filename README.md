[![Build Status](https://travis-ci.org/centic9/IntelliJ-Automation-Plugin.svg)](https://travis-ci.org/centic9/IntelliJ-Automation-Plugin) 
[![Gradle Status](https://gradleupdate.appspot.com/centic9/IntelliJ-Automation-Plugin/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/IntelliJ-Automation-Plugin/status)
[![Release](https://img.shields.io/github/release/centic9/IntelliJ-Automation-Plugin.svg)](https://github.com/centic9/IntelliJ-Automation-Plugin/releases)

Provides a plugin for IntelliJ to have a REST service available for running certain tasks in IntelliJ in an automated way.

This is mostly used in nightly update-jobs on developer machines for triggering a refresh/recompile of code after a checkout/merge onto the latest state from the source repository.

#### Getting started

##### Grab it

    git clone git://github.com/centic9/IntelliJ-Automation-Plugin

##### Build it using Gradle

    ./gradlew buildPlugin

It uses the Gradle plugin from https://github.com/JetBrains/gradle-intellij-plugin

#### Deploy it

The plugin is not (yet) published at the IntelliJ plugin repository, therefore you need to build and install it locally.

After building the plugin, there is a zip file at ´build/distributions´ which is the bundled plugin package. You can import this in IntelliJ via ´Plugins -> Install plugin from disk´.

#### Contribute

If you are missing things or have suggestions how to improve the plugin, please either send pull requests or create [issues](https://github.com/centic9/IntelliJ-Automation-Plugin/issues).

##### Run the plugin from the sources

    ./gradlew runIdea

#### Licensing
* IntelliJ-Automation-Plugin is licensed under the [BSD 2-Clause License].
* A few pieces are imported from other sources, the source-files contain the necessary license pieces/references.

[BSD 2-Clause License]: http://www.opensource.org/licenses/bsd-license.php
