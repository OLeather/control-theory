plugins {
    kotlin("multiplatform")
}

group = "com.github.oleather"
version = "0.1.0"

repositories {
    jcenter()
    maven("https://dl.bintray.com/mipt-npm/dev")
}

kotlin{
    explicitApiWarning()

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                api("space.kscience:kmath-core:0.2.1")
                api("space.kscience:kmath-functions:0.2.1")
                api("com.soywiz.korlibs.korma:korma:2.0.9")
                api("com.soywiz.korlibs.korma:korma-shape:2.0.9")
            }
        }
    }
}