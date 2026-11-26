plugins {
    alias(libs.plugins.bustan.kmp.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
        }
        androidUnitTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}
