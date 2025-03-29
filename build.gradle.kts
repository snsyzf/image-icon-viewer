import org.jetbrains.intellij.platform.gradle.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.platform.gradle.tasks.PublishPluginTask

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/spring")
        maven("https://www.jetbrains.com/intellij-repository/releases/")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/spring-plugin")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
//    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.intellij.platform") version "2.3.0"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
}

group = "com.gitee.plugins"
version = "1.0.12"

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/spring")
    maven("https://www.jetbrains.com/intellij-repository/releases/")
    intellijPlatform {
        defaultRepositories()
    }
    mavenCentral()
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3.4")
    }

    compileOnly(kotlin("stdlib"))
    implementation("com.twelvemonkeys.imageio:imageio-batik:3.12.0")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.18")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}


tasks.withType<PatchPluginXmlTask>() {
    this.pluginVersion.set(project.version.toString())
//    this.untilBuild.set("")
    this.changeNotes.set(file("changenotes.html").readText())
    this.pluginDescription.set(file("description.html").readText())
}

tasks.withType<PublishPluginTask>() {
    if (project.hasProperty("token")) {
        this.token.set(project.property("token").toString())
    }
}

