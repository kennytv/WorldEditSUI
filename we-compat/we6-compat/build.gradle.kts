plugins {
    java
}
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

group = "eu.kennytv.worldeditsui"
version = "1.7.3-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(rootProject.project(":we-compat:we-compat-common"))
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.4-SNAPSHOT")
    compileOnly("com.sk89q:worldguard:6.1.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper:paper-api:1.9.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.1.0")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
