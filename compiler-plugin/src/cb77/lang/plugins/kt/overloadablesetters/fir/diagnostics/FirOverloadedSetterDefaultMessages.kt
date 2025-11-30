package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_COLLECTION_OF_TYPES
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_TYPE

object FirOverloadedSetterDefaultMessages : BaseDiagnosticRendererFactory() {
	override val MAP by KtDiagnosticFactoryToRendererMap("FIRSetterOverloading") { map ->
		with (map) {
			put(
					FirOverloadedSetterErrors.SETTER_DECL_MUST_HAVE_SINGLE_PARAM,
					"Property setters must have exactly one parameter."
			)
			
			put(
					FirOverloadedSetterErrors.SETTER_DECL_MUST_RETURN_UNIT,
					"Property setters must return Unit."
			)
			
			put(
					FirOverloadedSetterErrors.SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE,
					"Overloaded setter parameter type ''{0}'' shadows the type of the property ''{1}''.",
					RENDER_TYPE,
					RENDER_TYPE
			)
			
			put(
				FirOverloadedSetterErrors.SETTER_ANNOTATED_PROP_NOT_MUTABLE,
				"''@HasCustomSetters'' can only be applied to mutable properties.",
			)
		}
	}
	
}