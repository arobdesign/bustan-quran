plugins {
    alias(libs.plugins.bustan.kmp.compose.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Material icons are not shipped by Compose Multiplatform 1.11.x; the Android
            // artifact is the only source of `Icons.*` for the screens written before P-18.
            implementation(libs.androidx.compose.material.icons.extended)
            implementation(project(":core:ui"))
            implementation(project(":core:data"))
            // `letter.forms_json` is a JSON column; parsed with the same kotlinx
            // runtime the rest of the app already ships.
            implementation(libs.kotlinx.serialization.json)
        }
        androidUnitTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}
