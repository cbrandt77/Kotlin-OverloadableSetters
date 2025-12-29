plugins {
	`kotlin-dsl`
}

fun DependencyHandlerScope.pluginImplementation(plugin: Provider<PluginDependency>, version: Provider<String>) {
	val _plugin = plugin.get()
	implementation(group=_plugin.pluginId, name="${_plugin.pluginId}.gradle.plugin", version=version.get())
}

dependencies {
	implementation(libs.nexus.publish)
	implementation(libs.build.config)
	
	pluginImplementation(libs.plugins.kotlin.jvm, libs.versions.kotlin)
	pluginImplementation(libs.plugins.kotlin.multiplatform, libs.versions.kotlin)
	pluginImplementation(libs.plugins.mavenPublish.vanniktech, libs.versions.mavenPublish.vanniktech)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

kotlin {
	jvmToolchain(21)
}