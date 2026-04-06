import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.varabyte.kobweb.application")
    id("com.varabyte.kobwebx.markdown")
}

group = "com.palodon.webclient"

kobweb {
    app {
        index {
            description.set("Palodon Web Client")
        }
    }
}

kotlin {
    configAsKobwebApplication("webclient")

    sourceSets {
        jsMain.dependencies {
            implementation(project(":shared-lib"))
            implementation("org.jetbrains.compose.html:html-core:1.10.0")
            implementation("org.jetbrains.compose.runtime:runtime:1.10.0")
            implementation("com.varabyte.kobweb:kobweb-core:0.24.0")
            implementation("com.varabyte.kobweb:kobweb-silk:0.24.0")
            implementation("com.varabyte.kobwebx:silk-icons-fa:0.24.0")
            implementation("com.varabyte.kobwebx:kobwebx-markdown:0.24.0")
        }
    }
}
