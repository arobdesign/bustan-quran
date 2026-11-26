package com.bustanquran.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convention plugin for a plain Kotlin Multiplatform library module
 * (Android + iOS, no Compose).
 *
 * Applies: `org.jetbrains.kotlin.multiplatform`, `com.android.library`, the
 * shared target set and the baseline multiplatform dependencies.
 */
class BustanKmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureAndroidLibrary(this)
            }

            extensions.configure<KotlinMultiplatformExtension> {
                configureKmpTargets(this)
            }

            addCommonMainDependencies()
            addCommonTestDependencies()
        }
    }
}
