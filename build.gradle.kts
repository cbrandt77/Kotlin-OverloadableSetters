plugins {
    id("root.publication")
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.build.config)
    alias(libs.plugins.gradle.plugin.publish) apply false
    alias(libs.plugins.binary.compatibility.validator) apply false
}

allprojects {
    group = "cb77.lang.plugins.kt.overloadablesetters"
    version = "1.0-SNAPSHOT"
}

