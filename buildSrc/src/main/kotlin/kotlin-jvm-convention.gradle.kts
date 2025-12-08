import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")
}

java {
	toolchain.languageVersion = JavaLanguageVersion.of(JAVA_VERSION)
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.fromTarget(JAVA_VERSION.toString()))
	}
}