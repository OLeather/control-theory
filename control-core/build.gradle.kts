plugins {
    kotlin("jvm")
}

group = "com.github.oleather"
version = "0.1.0"

repositories {
    jcenter()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://dl.bintray.com/mipt-npm/dev")
    maven("https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    api("kscience.kmath:kmath-core:0.2.0-dev-2")
    api("org.jetbrains.lets-plot-kotlin:lets-plot-kotlin-api:1.0.0")
    api("kscience.plotlykt:plotlykt-server:0.2.0")
}

