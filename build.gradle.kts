plugins {
    kotlin("jvm") version "1.4.10" apply false
}

group = "com.github.oleather"
version = "0.1.0"

repositories {
    jcenter()
}

allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}