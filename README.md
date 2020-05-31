[![Build Status](https://travis-ci.org/centic9/IntelliJ-Automation-Plugin.svg)](https://travis-ci.org/centic9/IntelliJ-Automation-Plugin) 
[![Gradle Status](https://gradleupdate.appspot.com/centic9/IntelliJ-Automation-Plugin/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/IntelliJ-Automation-Plugin/status)
[![Release](https://img.shields.io/github/release/centic9/IntelliJ-Automation-Plugin.svg)](https://github.com/centic9/IntelliJ-Automation-Plugin/releases)

Provides a plugin for IntelliJ to have a REST service available for running certain tasks in IntelliJ in an automated way.

This can be used in nightly update-jobs on developer machines for triggering a refresh/recompile of code after a checkout/merge onto the latest state from the source repository.

#### Getting started

##### Grab it

    git clone git://github.com/centic9/IntelliJ-Automation-Plugin

##### Build it using Gradle

    ./gradlew buildPlugin

It uses the Gradle plugin from https://github.com/JetBrains/gradle-intellij-plugin

#### Deploy it

The plugin is not (yet) published at the IntelliJ plugin repository, therefore you need to build and install it locally.

After building the plugin, there is a zip file at ´build/distributions´ which is the bundled plugin package. You can import this in IntelliJ via ´Plugins -> Install plugin from disk´. After a restart of IntelliJ there should be a REST server running on port 10081.

#### Configure it

There is a section "REST Automation" in the IntelliJ settings where you can change the port of the REST server. 

Changes should be applied immediately, no restart necessary.

#### Invoke it

The plugin currently only supports the actions `Version`, `Recompile`, `Compile` and `VcsRefresh`, but it is easy to add new ones, see [RESTService](https://github.com/centic9/IntelliJ-Automation-Plugin/blob/master/src/main/java/org/dstadler/intellij/automation/RESTService.java#L36) for the code and https://centic9.github.io/IntelliJ-Action-IDs/ for a list of available actions.

You can invoke this in a script via the following (curl can be installed on Windows via [Cygwin](https://cygwin.com/))

    curl http://localhost:10081/Recompile

#### Contribute

If you are missing things or have suggestions how to improve the plugin, please either send pull requests or create [issues](https://github.com/centic9/IntelliJ-Automation-Plugin/issues).

##### Run the plugin from the sources for testing

    ./gradlew runIdea

#### Licensing
* IntelliJ-Automation-Plugin is licensed under the [BSD 2-Clause License].
* A few pieces are imported from other sources, the source-files contain the necessary license pieces/references.

[BSD 2-Clause License]: http://www.opensource.org/licenses/bsd-license.php
