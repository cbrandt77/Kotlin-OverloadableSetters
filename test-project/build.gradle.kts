import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version libs.versions.kotlin
//	id("io.github.cbrandt77.kt.overloadablesetters") version "1.0.0"
}

repositories {
	gradlePluginPortal()
	mavenCentral()
//	mavenLocal()
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_21
	}
}