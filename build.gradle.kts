import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.10"
}



allprojects {
    group = "org.example"
    version = "1.0"


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

allprojects{
    apply(plugin = "java")
    apply(plugin = "kotlin")
    repositories {
        mavenCentral()
        maven(url="https://dl.bintray.com/kyonifer/maven")
        jcenter()
    }

    dependencies {
        add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("testCompile", "junit:junit:4.13")
        compile("com.kyonifer", "koma-core-ejml", "0.12")
        compile("com.kyonifer", "koma-plotting", "0.12")
    }
}