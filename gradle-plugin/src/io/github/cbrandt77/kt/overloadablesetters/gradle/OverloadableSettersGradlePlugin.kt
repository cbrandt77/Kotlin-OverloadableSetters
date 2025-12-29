/*
 * Copyright (C) 2025 Caleb Brandt
 *
 * This file is part of Overloadable Setters.
 *
 * Overloadable Setters is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overloadable Setters is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.cbrandt77.kt.overloadablesetters.gradle

import io.github.cbrandt77.kt.overloadablesetters.OverloadableSettersBuildConfig
import org.gradle.api.Project
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
    
    override fun getCompilerPluginId(): String = OverloadableSettersBuildConfig.KOTLIN_PLUGIN_ID
    
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
		    groupId = OverloadableSettersBuildConfig.KOTLIN_PLUGIN_GROUP,
		    artifactId = OverloadableSettersBuildConfig.KOTLIN_PLUGIN_NAME,
		    version = OverloadableSettersBuildConfig.KOTLIN_PLUGIN_VERSION,
    )
    
    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        
        kotlinCompilation.dependencies { implementation(OverloadableSettersBuildConfig.ANNOTATIONS_LIBRARY_COORDINATES) }
        if (kotlinCompilation.implementationConfigurationName == "metadataCompilationImplementation") {
            project.dependencies.add("commonMainImplementation", OverloadableSettersBuildConfig.ANNOTATIONS_LIBRARY_COORDINATES)
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