# Classification iOS App

You can find an in depth walkthrough for training a Core ML model [here](https://cloud-annotations.github.io/training/).

## Setup
`git clone` the repo and `cd` into it by running the following command:

```bash
git clone github.com/cloud-annotations/classification-android.git &&
cd classification-android
```

## Install Android Studio
The recommended way to develop applications for Android is by using Android Studio, which can be downloaded [here](https://developer.android.com/studio/index.html)

## Open the project with Android Studio
Launch Android Studio and choose **Open an existing Android Studio project**

![](https://codelabs.developers.google.com/codelabs/tensorflow-for-poets-2-tflite/img/1482ddc7911df61b.png)

In the file selector, choose `classification-android`.

You will get a **Gradle Sync** popup, the first time you open the project, asking about using gradle wrapper. Click **OK**.

![](https://codelabs.developers.google.com/codelabs/tensorflow-for-poets-2-tflite/img/b9f9a03dd27fd1bb.png)

## Set up an Android device
You can't load the app from android studio onto your phone unless you activate **developer mode** and **USB Debugging**. This is a one time setup process.

Follow these [instructions](https://developer.android.com/studio/debug/dev-options.html#enable).

## Add your model files to the project
Copy the `model_android` directory generated from the classification walkthrough and paste it into the `classification-android/app/src/main/assets` folder of this repo.

## Run the app
In Android Studio run a **Gradle sync** so the build system can find your files.

![](https://codelabs.developers.google.com/codelabs/tensorflow-for-poets-2-tflite/img/774326d4e89c2559.png)

Then hit play to start the build and install process.
