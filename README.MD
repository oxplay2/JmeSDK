JME-SDK
---
A completely custom SDK using JavaFX. **Requires Java 11**

This project is currently in its **CONCEPT** stage.

How to use:
* Pull this project. This is the SDK.
    * `git pull https://github.com/jayfella/JmeSDK.git`
* Pull the [sdk-ext](https://github.com/jayfella/sdk-ext) project. This is the SDK API. Required by both the SDK and SDK Projects.
    * `git pull https://github.com/jayfella/sdk-ext.git`
 * Pull the [TestProject](https://github.com/jayfella/TestProject). This project can be opened in the SDK and is used to test all of its available functionality.
    * `https://github.com/jayfella/TestProject.git`
* Build the sdk-ext project to mavenLocal
    * `cd ./sdk-ext`
    * `./gradlew build install`

To run the SDK either open it in your favourite IDE or navigate to the JmeSDK folder and execute `./gradlew run`.
