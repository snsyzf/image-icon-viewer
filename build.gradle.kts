buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/spring")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/spring-plugin")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.jetbrains.intellij") version "1.13.2"
    id("org.jetbrains.kotlin.jvm") version "1.7.22"
}

group = "com.gitee.plugins"
version = "1.0.11"

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/spring")
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    implementation("com.twelvemonkeys.imageio:imageio-batik:3.9.4")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    this.version.set("2022.3")
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    this.version.set(project.version.toString())
    this.sinceBuild.set("193.*")
    this.untilBuild.set("293.*")

    this.changeNotes.set(file("changenotes.html").readText())
    this.pluginDescription.set(file("description.html").readText())
}

tasks.getByName<org.jetbrains.intellij.tasks.PublishPluginTask>("publishPlugin") {
    if (project.hasProperty("token")) {
        this.token.set(project.property("token").toString())
    }
}
