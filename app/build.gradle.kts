import java.util.zip.ZipFile
import java.io.File

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.shivamkr12.rally"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shivamkr12.rally"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
            assets.srcDirs("src/main/assets")
        }
    }
}

val natives = configurations.create("natives")

dependencies {
    implementation(libs.androidx.appcompat)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // LibGDX dependencies
    val gdxVersion = "1.13.0"
    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

val unpackAndroidNatives = tasks.register("unpackAndroidNatives") {
    val destDir = layout.projectDirectory.dir("libs")
    val fileCollection = configurations.getByName("natives")
    
    // Resolve the files at configuration time to avoid passing the Configuration object itself
    val nativeFiles = fileCollection.files
    
    inputs.files(nativeFiles)
    outputs.dir(destDir)
    
    doLast {
        nativeFiles.forEach { jarFile ->
            val fileName = jarFile.name
            var architecture: String? = null
            if (fileName.endsWith("natives-arm64-v8a.jar")) architecture = "arm64-v8a"
            if (fileName.endsWith("natives-armeabi-v7a.jar")) architecture = "armeabi-v7a"
            if (fileName.endsWith("natives-x86_64.jar")) architecture = "x86_64"
            if (fileName.endsWith("natives-x86.jar")) architecture = "x86"

            if (architecture != null) {
                val archDir = destDir.dir(architecture).asFile
                archDir.mkdirs()
                ZipFile(jarFile).use { zip ->
                    zip.entries().asSequence().filter { it.name.endsWith(".so") }.forEach { entry ->
                        zip.getInputStream(entry).use { input ->
                            File(archDir, entry.name).outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        }
    }
}

tasks.whenTaskAdded {
    if (name.contains("merge") && name.contains("JniLibFolders")) {
        dependsOn(unpackAndroidNatives)
    }
}