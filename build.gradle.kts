plugins {
    id("org.jetbrains.intellij") version "0.7.2"
    kotlin("jvm") version "1.4.32"
}

group = "com.gitee.plugins"
version = "1.0.6"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2021.1"
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(file("changenotes.html").readText(Charsets.UTF_8))
    version(project.version)
    sinceBuild("193.*")
}
