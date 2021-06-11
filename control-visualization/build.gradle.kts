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
    implementation("space.kscience:plotlykt-server:0.4.2")
    api(project(":control-motion"))
    api(project(":control-core"))
    api(project(":control-models"))
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

kotlin{
    explicitApiWarning()
}