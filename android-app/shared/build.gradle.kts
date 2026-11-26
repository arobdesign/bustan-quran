import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.bustan.kmp.compose.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Navigation host for the whole app. Every screen still lives in the
            // androidMain source set of its feature module until Phase 3 moves the
            // composables to commonMain.
            implementation(project(":core:ui"))
            implementation(project(":core:data"))
            implementation(project(":core:audio"))
            implementation(project(":feature:onboarding"))
            implementation(project(":feature:quran"))
            implementation(project(":feature:hafalan"))
            implementation(project(":feature:games"))
            implementation(project(":feature:parent"))
            implementation(project(":feature:billing"))
            implementation(project(":feature:islamic"))

            implementation(libs.androidx.compose.material3.window)
            implementation(libs.koin.android)
        }
    }

    // `:shared` is the only module in the project that produces an iOS framework.
    // The iOS targets themselves are created by `bustan.kmp.library`, so they are
    // configured here rather than declared again.
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
}
