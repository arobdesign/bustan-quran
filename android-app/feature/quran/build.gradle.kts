plugins {
    alias(libs.plugins.bustan.kmp.compose.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:ui"))
            implementation(project(":core:data"))
            implementation(project(":core:audio"))
            implementation(libs.androidx.compose.material3.window)
            implementation(libs.androidx.compose.material.icons.extended)
        }
        androidUnitTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}
