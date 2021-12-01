buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/spring")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/spring-plugin")
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.intellij") version "1.3.0"
    kotlin("jvm") version "1.6.0"
}

group = "com.gitee.plugins"
version = "1.0.8"

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/spring")
}

dependencies {
    compileOnly(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    this.version.set("2021.2")
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    this.changeNotes.set(file("changenotes.html").readText(Charsets.UTF_8))
    this.version.set(project.version.toString())
    this.sinceBuild.set("193.*")
    this.untilBuild.set("213.*")
}
