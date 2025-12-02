plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("java-gradle-plugin")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("testResources"))
    }
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))

    testImplementation(kotlin("test-junit5"))
}

buildConfig {
    packageName(project.group.toString())

    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")

    val pluginProject = project(":compiler-plugin")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${pluginProject.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${pluginProject.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${pluginProject.version}\"")

    val annotationsProject = project(":plugin-annotations")
    buildConfigField(
        type = "String",
        name = "ANNOTATIONS_LIBRARY_COORDINATES",
        expression = "\"${annotationsProject.group}:${annotationsProject.name}:${annotationsProject.version}\""
    )
}

gradlePlugin {
    plugins {
        create("OverloadableSettersPlugin") {
            id = rootProject.group.toString()
            displayName = "OverloadableSettersPlugin"
            description = "Add custom setters for your Kotlin properties with different types, like setting a String field with an Int or a Char."
            implementationClass = "cb77.lang.plugins.kt.overloadablesetters.gradle.OverloadableSettersGradlePlugin"
        }
    }
}
