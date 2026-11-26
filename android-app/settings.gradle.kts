pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BustanQuran"

// Android entry point (thin: Activity + Application only).
include(":app")
// Umbrella KMP module. The ONLY module that produces the iOS framework used by iosApp.
include(":shared")

// Shared KMP modules.
include(":core:ui")
include(":core:data")
include(":core:audio")
include(":core:testing")

// Feature KMP modules.
include(":feature:onboarding")
include(":feature:quran")
include(":feature:hafalan")
include(":feature:games")
include(":feature:parent")
include(":feature:billing")
include(":feature:islamic")

// JVM tooling, not shipped in any app.
include(":tools:content-import")
