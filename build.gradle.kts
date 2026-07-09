import org.jetbrains.intellij.platform.gradle.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.platform.gradle.tasks.PublishPluginTask

plugins {
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
}

group = "com.gitee.plugins"
version = "1.0.19"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.2.4")
    }

    compileOnly(kotlin("stdlib"))
}

// instrumentCode expects a macOS-style JDK layout with a "Packages" directory.
// Standard Windows JDKs (including Microsoft Build of OpenJDK) do not provide it.
tasks.named("instrumentCode") {
    doFirst {
        val packagesDir = file("${System.getProperty("java.home")}/Packages")
        if (!packagesDir.exists()) {
            packagesDir.mkdirs()
        }
    }
}

intellijPlatform {
    buildSearchableOptions.set(false)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<PatchPluginXmlTask> {
    pluginVersion.set(project.version.toString())
    changeNotes.set(file("changenotes.html").readText())
    pluginDescription.set(file("description.html").readText())
}

tasks.withType<PublishPluginTask> {
    if (project.hasProperty("token")) {
        token.set(project.property("token").toString())
    }
}
