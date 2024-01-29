# TV-Remote-App
TV Remote over Wifi made using ConnectSDK👉

ConnectSDK is great. When I got started, I couldn't find a single reliable project (besides the official one) I could follow to really understand how it works. 
Hence, I'm making this. It's been frustrating. Very frustrating. But I love it. If it helps you, consider leaving a like I guess.
On to more serious matters💯

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

# Errors To Avoid❌
There's a lot of programming languages out there. Programmers often pick one or more to specialize in. Regardless of which language you use, the one constant they all share is errors. These can be minute like forgetting to initialize a variable (By the way make sure to always do that) to soul stressing ones like assigning the id for a LinearLayout to AlertDialog making your entire program crash and you spend your time debugging the logic meanwhile it's a UI bug. 

The point is, errors suck. Especially when they don't tell you what went wrong. These are the mistakes I made when creating this. Hope it helps.📜

1. The permissions we need for the app are granted at install. We don't need any special permissions from the user to discover and connect to devices. You don't have to check for permissions from the device.
  
2. **@Override** will throw the error "Method does not override from its superclass" if you're **NOT** overriding from its superclass.   <br> Basically, make sure that the methods you use actually belong to the class that you're using. e.g **DiscoveryManager** inherits **OnDeviceAdded()**. If you used the method in your code and tried to **@Override** it, it will throw that error since the method is **inherited** (See **Inherited Methods** at https://connectsdk.com/en/latest/apis-and/and-discoverymanager.html) it does not belong to DiscoveryManager.

3. Your app crashing and telling you what to do > Just not working.
   If your app crashes, pause logcat (Be careful with this! Make sure to unpause it once you're done. There's a reason.), look for **Tabe.CrashHandler** in the logs, scroll to the far right of it's log and you'll see **EMessage**. That will tell you exactly why the app crashed and you can fix it. Trust me **Tabe** will save your life more than once.💯

4. Not an error but an honourable mention. The **Debugger** is a fantastic tool and you should definitely learn how to use it. It gives you insight into exactly what's happening in your code. ✅
