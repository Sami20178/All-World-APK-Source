# All World APK Source

This private repository contains the Android source/build project for the single output artifact **All-World-icon-fixed-install.apk**.

## Embedded splash
The splash screen is implemented in `app/src/main/java/com/allworld/app/MainActivity.java` as native Android UI. It does not fetch a splash image or HTML file. The splash source is in this repository and is compiled into the APK.

## Build
The workflow `.github/workflows/build-apk.yml` builds the app, verifies APK ZIP integrity, verifies the Android APK signature with `apksigner`, checks package metadata, and uploads an artifact named `All-World-icon-fixed-install`.

Open the repository's **Actions** tab, select **Build All-World-icon-fixed-install.apk**, and run **Run workflow**. After the workflow succeeds, open that run and download its artifact.

## Important facts
- The APK file named `All-World-icon-fixed-install.apk` was not present in the repository when this project was created. This is a source-based rebuild, not a byte-for-byte patch of that original binary.
- The app uses package ID `com.allworld.app`, min SDK 28 (Android 9 / Fire OS 7), and a fresh debug signing key from GitHub Actions. It cannot be assumed to update an installed copy signed by another key. Do not uninstall an existing app unless you accept losing its local data.
- A successful CI build and signature verification do not prove installation on a physical Fire tablet. The downloaded APK still needs an on-device install test.
