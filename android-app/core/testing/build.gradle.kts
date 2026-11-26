plugins {
    alias(libs.plugins.bustan.kmp.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Exposed to test compilations of consuming modules, never to their main
            // source set: consumers add this module to `androidUnitTest` only.
            api(libs.junit)
            api(libs.turbine)
            api(libs.mockk)
            api(libs.kotlinx.coroutines.test)
            api(libs.androidx.junit)
        }
    }
}
