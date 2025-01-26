plugins {
    id("application")
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.1")

    // Добавляем зависимости для Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}

application {
    mainClass.set("mainpackage.Main")
}

tasks.named<Jar>("shadowJar") {
    archiveBaseName.set("rogue")
    archiveVersion.set("1.0")
    manifest {
        attributes("Main-Class" to "mainpackage.Main")
    }
}

