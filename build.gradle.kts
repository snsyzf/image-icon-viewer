buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.jetbrains.intellij") version "1.7.0"
    kotlin("jvm") version "1.6.20"
}

group = "com.gitee.plugins"
version = "1.0.10"

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
    this.version.set("2022.2")
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    this.version.set(project.version.toString())
    this.sinceBuild.set("193.*")
    this.untilBuild.set("222.*")
}
