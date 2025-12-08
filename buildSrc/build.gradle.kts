plugins {
	`kotlin-dsl`
}

dependencies {
	implementation(libs.nexus.publish)
	implementation(libs.build.config)
	
	libs.plugins.kotlin.jvm.get().run {
		implementation(group=pluginId, name="${pluginId}.gradle.plugin", version=libs.versions.kotlin.get())
	}
	libs.plugins.kotlin.multiplatform.get().run {
		implementation(group=pluginId, name="${pluginId}.gradle.plugin", version=libs.versions.kotlin.get())
	}
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

kotlin {
	jvmToolchain(21)
}