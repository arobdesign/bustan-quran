plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.bustanquran.tools.ContentImportCliKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation("junit:junit:4.13.2")
}
