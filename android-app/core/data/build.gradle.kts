plugins {
    alias(libs.plugins.bustan.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)
            implementation(libs.androidx.datastore.proto)
            implementation(libs.kotlinx.serialization.json)
        }
        androidUnitTest.dependencies {
            implementation(project(":core:testing"))
        }
    }
}

dependencies {
    // Room code generation runs on the Android target only; the databases still
    // live in androidMain until the Room KMP migration moves them to commonMain.
    add("kspAndroid", libs.androidx.room.compiler)
}
