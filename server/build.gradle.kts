plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
}

repositories {
    mavenCentral()
}

dependencies {
    // Bypassing Quarkus KMP workspace bug by depending on the built JAR directly
    implementation(files("../shared-lib/build/libs/shared-lib-jvm-1.0-SNAPSHOT.jar"))
    implementation(platform("io.quarkus.platform:quarkus-bom:3.17.0"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-jackson")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkiverse.jdbc:quarkus-jdbc-sqlite:3.0.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    compilerOptions.javaParameters.set(true)
    compilerOptions.freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

val jvmJarTask = tasks.getByPath(":shared-lib:jvmJar")
tasks.named("compileKotlin") {
    dependsOn(jvmJarTask)
}

tasks.named("test") {
    dependsOn(jvmJarTask)
}

tasks.matching { it.name.startsWith("quarkus") }.configureEach {
    dependsOn(jvmJarTask)
}