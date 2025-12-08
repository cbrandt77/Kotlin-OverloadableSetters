import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version libs.versions.kotlin
//	id("cb77.lang.plugins.kt.overloadablesetters") version "1.0-SNAPSHOT"
}

repositories {
	mavenCentral()
	mavenLocal()
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_21
	}
}