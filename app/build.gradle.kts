import java.util.Locale

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  jacoco
}

android {
  namespace = "com.kodeco.cocktails"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.kodeco.cocktails"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug{
      enableAndroidTestCoverage = true
      enableUnitTestCoverage = true
    }
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.21")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

  // coil
  implementation("io.coil-kt:coil-compose:2.4.0")

  // compose dependencies
  implementation(platform("androidx.compose:compose-bom:2023.08.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  // Retrofit with Moshi Converter
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
  // make returns easier to deal with for retrofit
  implementation("com.github.skydoves:sandwich:2.0.0")
  implementation("com.github.skydoves:sandwich-retrofit:2.0.0")

  implementation("androidx.datastore:datastore-preferences:1.0.0")

  testImplementation("junit:junit:4.13.2")
  testImplementation("androidx.arch.core:core-testing:2.2.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}

val exclusions = listOf(
  "**/R.class",
  "**/R\$*.class",
  "**/BuildConfig.*",
  "**/Manifest*.*",
  "**/*Test*.*",
  "**/*Activity.*",
  "**/*Activity*.*",
  "android/**/*.*"
)

tasks.withType(Test::class) {
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

android {
    val unitTests = "testDebugUnitTest"
    tasks.create<JacocoReport>("JacocoTest") {
      dependsOn(listOf(unitTests))
      group = "Reporting"
      description = "Execute ui and unit tests, generate and combine Jacoco coverage report"
      reports {
        xml.required.set(true)
        html.required.set(true)
      }
      sourceDirectories.setFrom(layout.projectDirectory.dir("src/main/java"))
      classDirectories.setFrom(files(
        fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
          exclude(exclusions)
        },
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
          exclude(exclusions)
        }
      ))
      executionData.setFrom(files(
        fileTree(layout.buildDirectory) { include(listOf("**/*.exec")) }
      ))
    }
}