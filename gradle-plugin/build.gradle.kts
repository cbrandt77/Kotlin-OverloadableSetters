plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "2.0.0"
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
    
    className = "OverloadableSettersBuildConfig"
    
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
    
    buildConfigField("String", "OPT_USECAMELCASE_CLINAME", "\"camelcase\"")
    buildConfigField("String", "OPT_SETTERPATTERN_CLINAME", "\"setter-pattern\"")
}

buildConfig {
    packageName(project.group.toString())
    
    useKotlinOutput {
        internalVisibility = true
    }
    
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
    vcsUrl = "https://github.com/cbrandt77/Kotlin-OverloadableSetters.git"
    website = "https://github.com/cbrandt77/Kotlin-OverloadableSetters"
    
    plugins {
        create("OverloadableSettersPlugin") {
            id = rootProject.group.toString()
            displayName = "OverloadableSettersPlugin"
            description = "Add custom setters for your Kotlin properties with different types, like setting a String field with an Int or a Char."
            implementationClass = "cb77.lang.plugins.kt.overloadablesetters.gradle.OverloadableSettersGradlePlugin"
        }
    }
}
