package cb77.lang.plugins.kt.overloadablesetters.gradle

import cb77.lang.plugins.kt.overloadablesetters.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused") // Used via reflection.
class OverloadableSettersGradlePlugin : KotlinCompilerPluginSupportPlugin {
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
        
        return project.provider { emptyList() }
    }
}