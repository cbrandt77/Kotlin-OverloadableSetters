plugins {
    id("kotlin-jvm-convention")
    `java-test-fixtures`
    id("com.github.gmazzo.buildconfig")
    alias(libs.plugins.shadow)
    idea
    id("module.publication")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    testFixtures {
        java.setSrcDirs(listOf("test-fixtures"))
    }
    test {
        java.setSrcDirs(listOf("test", "test-gen"))
        resources.setSrcDirs(listOf("testData"))
    }
}

idea {
    module.generatedSourceDirs.add(projectDir.resolve("test-gen"))
}

val annotationsRuntimeClasspath: Configuration by configurations.creating { isTransitive = false }



val embedded by configurations.dependencyScope("embedded")

val embeddedClasspath by configurations.resolvable("embeddedClasspath") { extendsFrom(embedded) }

configurations.named("compileOnly").configure { extendsFrom(embedded) }

configurations.named("testImplementation").configure { extendsFrom(embedded) }

dependencies {
    compileOnly(kotlin("compiler"))

    testFixturesApi(kotlin("test-junit5"))
    testFixturesApi(kotlin("compiler-internal-test-framework"))
    testFixturesApi(kotlin("compiler"))

    annotationsRuntimeClasspath(project(":plugin-annotations"))

    // Dependencies required to run the internal test framework.
    testRuntimeOnly(libs.junit)
    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly(kotlin("test"))
    testRuntimeOnly(kotlin("script-runtime"))
    testRuntimeOnly(kotlin("annotations-jvm"))
    
    embedded(project(":compiler-compat"))
    rootProject.isolated.projectDirectory.dir("compiler-compat").asFile.listFiles()?.forEach {
        if (it.isDirectory && it.name.startsWith("k")) {
            embedded(project(":compiler-compat:${it.name}"))
        }
    }
}

for (c in arrayOf("apiElements", "runtimeElements")) {
    configurations.named(c) { artifacts.removeIf { true } }
    artifacts.add(c, tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "noCompat"
}



buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }
}

tasks.shadowJar {
//    from(java.sourceSets.main.map { it.output })
    configurations.set(setOf(embeddedClasspath))
    
    archiveClassifier = ""
    
    dependencies {
        exclude(dependency("org.jetbrains:.*"))
        exclude(dependency("org.intellij:.*"))
        exclude(dependency("org.jetbrains.kotlin:.*"))
        exclude(dependency("dev.drewhamilton.poko:.*"))
    }
    
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    
    relocate("dev.zacsweers.metro", "${project.group}.shaded.dev.zacsweers.metro")
}

tasks.build {
    dependsOn(tasks.test)
}

tasks.test {
    dependsOn(annotationsRuntimeClasspath)

    useJUnitPlatform()
    workingDir = rootDir

    systemProperty("annotationsRuntime.classpath", annotationsRuntimeClasspath.asPath)

    // Properties required to run the internal test framework.
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")

    systemProperty("idea.ignore.disabled.plugins", "true")
    systemProperty("idea.home.path", rootDir)
}
java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

kotlin {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
        freeCompilerArgs.addAll("-Xcontext-parameters")
    }
}

val generateTests by tasks.registering(JavaExec::class) {
    inputs.dir(layout.projectDirectory.dir("testData"))
        .withPropertyName("testData")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(layout.projectDirectory.dir("test-gen"))
        .withPropertyName("generatedTests")

    classpath = sourceSets.testFixtures.get().runtimeClasspath
    mainClass.set("org.jetbrains.kotlin.compiler.plugin.template.GenerateTestsKt")
    workingDir = rootDir
}

tasks.compileTestKotlin {
    dependsOn(generateTests)
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations
        .testRuntimeClasspath.get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}

buildConfig {
    packageName(project.group.toString())
    
    className = "OverloadableSettersBuildConfig"
    
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
    
    buildConfigField("String", "OPT_USECAMELCASE_CLINAME", "\"camelcase\"")
    buildConfigField("String", "OPT_SETTERPATTERN_CLINAME", "\"setter-pattern\"")
}

plugins.withId("org.gradle.java-test-fixtures") {
    val component = components["java"] as AdhocComponentWithVariants
    
    configurations.findByName("testFixturesApiElements")?.let {
        component.withVariantsFromConfiguration(it) {
            skip()
        }
    }
    configurations.findByName("testFixturesRuntimeElements")?.let {
        component.withVariantsFromConfiguration(it) {
            skip()
        }
    }
    
    
    // Workaround to not publish test fixtures sources added by com.vanniktech.maven.publish plugin
    // TODO: Remove as soon as https://github.com/vanniktech/gradle-maven-publish-plugin/issues/779 closed
    afterEvaluate {
        configurations.findByName("testFixturesSourcesElements")?.let {
            component.withVariantsFromConfiguration(it) { skip() }
        }
    }
}


//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = "compiler-plugin"
//            from(components["java"])
//        }
//    }
//}
