buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("javax.xml.bind:jaxb-api:2.3.1")
        classpath("com.sun.xml.bind:jaxb-core:2.3.0.1")
        classpath("com.sun.xml.bind:jaxb-impl:2.3.2")
    }
}

plugins {
    // Defining plugins here with `apply false` puts them on the root classpath
    // which effectively "installs" them for all subprojects to use without
    // needing to specify the version again.
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm") version "2.3.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.10" apply false
    id("org.jetbrains.compose") version "1.10.0" apply false
    id("io.quarkus") version "3.17.0" apply false
    id("com.varabyte.kobweb.application") version "0.24.0" apply false
    id("com.varabyte.kobweb.library") version "0.24.0" apply false
    id("com.varabyte.kobwebx.markdown") version "0.24.0" apply false
}

allprojects {
    group = "com.palodon"
    version = "1.0-SNAPSHOT"
}