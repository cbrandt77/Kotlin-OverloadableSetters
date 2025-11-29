package cb77.lang.plugins.kt.overloadablesetters.fir

import cb77.lang.plugins.kt.overloadablesetters.fir.extensions.AssignmentTransformer
import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadedSetterErrors
import cb77.lang.plugins.kt.overloadablesetters.fir.extensions.FirDefaultSetterGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class FirRegistrar_OverloadableSetters(private val annotations: Collection<String>) : FirExtensionRegistrar() {
	override fun ExtensionRegistrarContext.configurePlugin() {
		+FirSetterOverloadFinderService.getFactory(annotations)
		+::AssignmentTransformer
		+::FirDefaultSetterGenerator
		+::FirOverloadableSettersCheckersExtension
		
		registerDiagnosticContainers(FirOverloadedSetterErrors)
	}
}