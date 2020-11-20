plugins {
    kotlin("jvm")
}

group = "com.github.oleather"
version = "0.1.0"

repositories {
    jcenter()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://dl.bintray.com/mipt-npm/dev")
    maven("https://jetbrains.bintray.com/lets-plot-maven")
}

dependencies {
    api(project(":control-core"))
}