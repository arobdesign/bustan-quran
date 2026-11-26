plugins {
    alias(libs.plugins.bustan.android.application)
}

android {
    // Everything else (namespace, SDK levels, Java 17, compose, release build type)
    // comes from the `bustan.android.application` convention plugin.
}

dependencies {
    // The only module the app knows about: it owns the iOS framework, the router
    // and the Koin graph.
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
}
