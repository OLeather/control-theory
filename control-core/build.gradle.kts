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
                api("kscience.kmath:kmath-core:0.2.0-dev-2")
                api("kscience.kmath:kmath-functions:0.2.0-dev-2")
            }
        }
    }
}