import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.10"
}



allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "org.example"
    version = "1.0"

    repositories {
        mavenCentral()
        maven(url = "https://dl.bintray.com/kyonifer/maven")
        maven("https://dl.bintray.com/mipt-npm/dataforge")
        maven("https://dl.bintray.com/mipt-npm/kscience")
        maven("https://dl.bintray.com/mipt-npm/dev")
        jcenter()
    }

    dependencies {
        add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("testCompile", "junit:junit:4.13")
        compile("com.kyonifer", "koma-core-ejml", "0.12")
        compile("com.kyonifer", "koma-plotting", "0.12")
        implementation("kscience.plotlykt:plotlykt-server:0.2.0")
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}