package cb77.lang.plugins.kt.overloadablesetters.fir

import cb77.lang.plugins.kt.overloadablesetters.fir.extensions.transformers.FirSetterAssignmentAlterer
import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
import cb77.lang.plugins.kt.overloadablesetters.fir.extensions.generators.FirSetterDefaultSetterGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class FirRegistrar_OverloadableSetters(private val annotations: Collection<String>) : FirExtensionRegistrar() {
	override fun ExtensionRegistrarContext.configurePlugin() {
		+FirSetterOverloadFinderService.getFactory(annotations)
		+::FirSetterAssignmentAlterer
		+::FirSetterDefaultSetterGenerator
		+::FirOverloadableSettersCheckersExtension
		
		registerDiagnosticContainers(FirOverloadableSetters_ErrorTypes)
	}
}