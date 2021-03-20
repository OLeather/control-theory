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
                api(project(":control-core"))
            }
        }
    }
}