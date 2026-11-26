plugins {
    alias(libs.plugins.bustan.kmp.compose.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Material icons are not shipped by Compose Multiplatform past 1.7.x,
            // so the Android artifact is used directly for now.
            implementation(libs.androidx.compose.material.icons.extended)
        }
        androidUnitTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}

dependencies {
    // Debug-only: the P-18 design catalog activity (`src/debug`) hosts Compose
    // content, which needs an Activity that can own a composition.
    add("debugImplementation", libs.androidx.activity.compose)
}

