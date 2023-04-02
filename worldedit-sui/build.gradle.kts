plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "eu.kennytv.worldeditsui"
version = "1.7.3-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}


dependencies {
    implementation(rootProject.project(":we-compat:we-compat-common"))
    implementation(rootProject.project(":we-compat:we6-compat"))
    implementation(rootProject.project(":we-compat:we7-compat"))
    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.13")
   // compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.1.0")
}
tasks.named<Copy>("processResources") {
    inputs.property("version", version)
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}

description = "WorldEditSUI"

tasks {
    shadowJar {
        archiveFileName.set("${project.name}.${archiveExtension.getOrElse("jar")}")
        // https://github.com/johnrengelman/shadow/issues/107
        isZip64 = true

        relocate("org.bstats", "eu.kennytv.worldeditsui.bstats")
    }
}
