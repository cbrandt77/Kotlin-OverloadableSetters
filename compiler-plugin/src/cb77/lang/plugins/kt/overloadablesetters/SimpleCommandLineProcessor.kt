package cb77.lang.plugins.kt.overloadablesetters

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
