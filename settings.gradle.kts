pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "OverloadableSetters"

include("compiler-plugin")
include("gradle-plugin")
include("plugin-annotations")
