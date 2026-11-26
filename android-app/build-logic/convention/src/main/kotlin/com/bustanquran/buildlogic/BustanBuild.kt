package com.bustanquran.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Single source of truth for the Android SDK range of every module. */
internal const val COMPILE_SDK = 36
internal const val MIN_SDK = 26
internal const val TARGET_SDK = 36

internal val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Namespace budget: every module derives its namespace from its Gradle path so
 * no module has to repeat it, e.g. `:core:ui` -> `com.bustanquran.core.ui`.
 * Kotlin package names stay whatever the source files declare.
 */
internal fun Project.derivedNamespace(): String = "com.bustanquran" + path.replace(':', '.')

/**
 * The one place where the Kotlin Multiplatform targets are declared.
 *
 * Every KMP module gets the same Android target plus the two iOS targets.
 * `iosX64` is deliberately absent: Compose Multiplatform 1.12.x and the JetBrains
 * lifecycle/navigation libraries no longer publish x86_64 simulator artifacts, so
 * the iOS simulator is Apple-silicon only. iOS target setup is identical in every
 * module so `:shared` can link a single framework containing all of them.
 */
internal fun Project.configureKmpTargets(kotlin: KotlinMultiplatformExtension) {
    kotlin.androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    kotlin.iosArm64()
    kotlin.iosSimulatorArm64()
}

/** Android DSL shared by every KMP library module. */
internal fun Project.configureAndroidLibrary(android: LibraryExtension) {
    android.namespace = derivedNamespace()
    android.compileSdk = COMPILE_SDK
    android.defaultConfig {
        minSdk = MIN_SDK
    }
    android.compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

/** Dependencies every KMP module needs, added to commonMain. */
internal fun Project.addCommonMainDependencies() {
    val catalog = versionCatalog
    dependencies.add("commonMainImplementation", catalog.findLibrary("kotlinx-coroutines-core").get())
    dependencies.add("commonMainImplementation", catalog.findLibrary("koin-core").get())
}

/** Dependencies every KMP module needs for its unit tests. */
internal fun Project.addCommonTestDependencies() {
    val catalog = versionCatalog
    dependencies.add("commonTestImplementation", catalog.findLibrary("kotlin-test").get())
    dependencies.add("commonTestImplementation", catalog.findLibrary("kotlinx-coroutines-test").get())
    dependencies.add("androidUnitTestImplementation", catalog.findLibrary("junit").get())
}
