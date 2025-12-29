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

package io.github.cbrandt77.kt.overloadablesetters

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@Suppress("unused") // Used via reflection.
class SimpleCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = OverloadableSettersBuildConfig.KOTLIN_PLUGIN_ID
    
    companion object {
        private const val OPTION_CAMELCASE = "camelcase"
    }

    override val pluginOptions: Collection<CliOption> = listOf(
//            CliOption(
//                    optionName = OPTION_CAMELCASE,
//                    valueDescription = "",
//                    description = """
//                        Allow setters with the form `setBar` to be detected, not just `set-bar`.
//
//                        This is disabled by default, as both `Foo.bar` and `Foo.Bar` will correspond to the same `Foo.setBar` method, potentially causing issues.
//                        When this option is set, any ambiguous properties will require their setter methods to be invoked directly.
//
//                        This option is only tested in English.
//                    """.trimIndent(),
//                    required = false
//            )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
//        when (option.optionName) {
//            OPTION_CAMELCASE -> {
//                OverloadedSetterOptions.allowAmbiguousCasePropertyNames = true
//                OverloadedSetterOptions.setterNameRegex = OverloadedSetterOptions.setterNameRegex.
//            }
//            else -> error("Unexpected config option: '${option.optionName}'")
//        }
    }
}
