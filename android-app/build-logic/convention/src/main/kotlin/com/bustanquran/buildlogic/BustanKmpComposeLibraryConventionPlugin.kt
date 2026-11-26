package com.bustanquran.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for a Kotlin Multiplatform module that renders Compose UI.
 *
 * Everything [BustanKmpLibraryConventionPlugin] does, plus the Compose compiler
 * and the Compose Multiplatform runtime / UI / Navigation / ViewModel stack.
 * Dependencies are compile-only per module: modules declare what they use, this
 * plugin only guarantees the shared foundation is present and version-aligned.
 */
class BustanKmpComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("bustan.kmp.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            val catalog = versionCatalog
            listOf(
                "compose-runtime",
                "compose-foundation",
                "compose-ui",
                "compose-material3",
                "androidx-lifecycle-viewmodel-compose-kmp",
                "androidx-lifecycle-runtime-compose-kmp",
                "androidx-navigation-compose-kmp",
                "koin-compose",
                "koin-compose-viewmodel",
                "koin-core-viewmodel",
            ).forEach { alias ->
                dependencies.add("commonMainImplementation", catalog.findLibrary(alias).get())
            }
        }
    }
}
