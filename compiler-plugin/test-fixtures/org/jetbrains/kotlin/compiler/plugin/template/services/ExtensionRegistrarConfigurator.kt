package org.jetbrains.kotlin.compiler.plugin.template.services

import cb77.lang.plugins.kt.overloadablesetters.fir.FirRegistrar_OverloadableSetters
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

fun TestConfigurationBuilder.configurePlugin() {
    useConfigurators(::ExtensionRegistrarConfigurator)
    configureAnnotations()
}

private class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    companion object {
        private val TEST_ANNOTATIONS = listOf(
                "HasCustomSetters",
                "qualified.HasCustomSetters"
        )
    }
    
    
    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) {
        FirExtensionRegistrarAdapter.registerExtension(FirRegistrar_OverloadableSetters(TEST_ANNOTATIONS))
//        IrGenerationExtension.registerExtension(SimpleIrGenerationExtension())
    }
}
