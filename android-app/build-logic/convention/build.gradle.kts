import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.bustanquran.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // The plugin implementations must be on the classpath so the convention
    // plugins can reference AGP / KGP types. They are provided at runtime by the
    // root build, which declares every plugin id with `apply false`.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "bustan.kmp.library"
            implementationClass = "com.bustanquran.buildlogic.BustanKmpLibraryConventionPlugin"
        }
        register("kmpComposeLibrary") {
            id = "bustan.kmp.compose.library"
            implementationClass = "com.bustanquran.buildlogic.BustanKmpComposeLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "bustan.android.application"
            implementationClass = "com.bustanquran.buildlogic.BustanAndroidApplicationConventionPlugin"
        }
    }
}
