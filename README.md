# TV-Remote-App
TV Remote over Wifi made using ConnectSDK.👉

ConnectSDK is great. When I got started, I couldn't find a single reliable project (besides the official one) I could follow to really understand how it works. 
Hence, I'm making this. It's been frustrating. Very frustrating. But I love it. If it helps you, consider leaving a like I guess.
On to more serious matters.💯

# SETUP 
1. Make sure to add this into **dependencyResolutionManagement** in your `settings.gradle.kts`  file:
  ```kotlin
  repositories {  
      google()  
      mavenCentral()  
      maven { url = uri("https://jitpack.io") }  
      // other repositories...  
  }
  ```

2. This should be added under **dependencies** in your **Module** `build.gradle.kts` file.  There are two **build.gradle** files in your project. You can tell which is which     by looking at the brackets beside the name. It should be `(Module :app)`.  You should see some dependencies there already:
  ```kotlin
  implementation("com.github.ConnectSDK:Connect-SDK-Android:master-SNAPSHOT")

  or

  implementation 'com.github.ConnectSDK:Connect-SDK-Android:master-SNAPSHOT'
  ```
**NOTE: The first line was the one that worked for me. I've heard from others that the second one works as well. Choose your poison.**

3. Once you've added those two, **SYNC** your Gradle files. It'll download everything you need to get started.🌍
