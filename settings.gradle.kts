pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
        maven("https://redirector.kotlinlang.org/maven/bootstrap")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
        // Publications used by IJ
        // https://kotlinlang.slack.com/archives/C7L3JB43G/p1757001642402909
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "OverloadableSetters"

include("compiler-plugin")
include("gradle-plugin")
include("plugin-annotations")
include("compiler-compat")
includeBuild("test-project")

rootProject.projectDir.resolve("compiler-compat").listFiles()!!.forEach {
    if (it.isDirectory && it.name.startsWith("k")) {
        include(":compiler-compat:${it.name}")
    }
}