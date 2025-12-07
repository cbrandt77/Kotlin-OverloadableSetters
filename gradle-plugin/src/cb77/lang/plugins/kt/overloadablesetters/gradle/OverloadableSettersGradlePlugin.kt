package cb77.lang.plugins.kt.overloadablesetters.gradle

import cb77.lang.plugins.kt.overloadablesetters.OverloadableSettersBuildConfig as BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused") // Used via reflection.
class OverloadableSettersGradlePlugin : KotlinCompilerPluginSupportPlugin {
//    abstract class Extension {
//        /**
//         * Allow setters with the form `setBar` to be detected, not just `set-bar`.
//         *
//         * This is disabled by default, as both `Foo.bar` and `Foo.Bar` will correspond to the same `Foo.setBar` method, potentially causing issues.
//         * When this option is set, any ambiguous properties will require their setter methods to be invoked directly.
//         *
//         * This option is only tested in English.
//         */
//        abstract val allowCamelCase: Property<Boolean>
//
//        /**
//         * The [Regex] pattern used to detect setters.
//         * Must contain a single capture group to capture the name of the target property.
//         */
//        abstract val setterNamePattern: Property<Regex?>
//    }
    
    override fun apply(target: Project) {
    
    }
    
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
    
    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID
    
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
		    groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
		    artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
		    version = BuildConfig.KOTLIN_PLUGIN_VERSION,
    )
    
    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        
        kotlinCompilation.dependencies { implementation(BuildConfig.ANNOTATIONS_LIBRARY_COORDINATES) }
        if (kotlinCompilation.implementationConfigurationName == "metadataCompilationImplementation") {
            project.dependencies.add("commonMainImplementation", BuildConfig.ANNOTATIONS_LIBRARY_COORDINATES)
        }
        
//        val ext = project.extensions.getByType(Extension::class.java)
        
        return project.provider {
            listOf()
//            listOf(
//                    SubpluginOption("", ext.allowCamelCase.orNull)
//            )
        }
    }
}