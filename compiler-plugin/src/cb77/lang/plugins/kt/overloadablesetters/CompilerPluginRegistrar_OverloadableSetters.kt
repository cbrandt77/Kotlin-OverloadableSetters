package cb77.lang.plugins.kt.overloadablesetters

import cb77.lang.plugins.kt.overloadablesetters.fir.FirRegistrar_OverloadableSetters
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class CompilerPluginRegistrar_OverloadableSetters : CompilerPluginRegistrar() {
	
	override val supportsK2: Boolean
		get() = true
	
	override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
		FirExtensionRegistrarAdapter.registerExtension(FirRegistrar_OverloadableSetters())
	}
}
