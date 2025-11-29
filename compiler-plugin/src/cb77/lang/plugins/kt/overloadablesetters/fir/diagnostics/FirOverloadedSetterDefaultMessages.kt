package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadedSetterErrors.DECL_SETTER_MUST_HAVE_ONE_PARAM
import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadedSetterErrors.DECL_SETTER_MUST_RETURN_UNIT
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_COLLECTION_OF_TYPES
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_TYPE

object FirOverloadedSetterDefaultMessages : BaseDiagnosticRendererFactory() {
	override val MAP by KtDiagnosticFactoryToRendererMap("FIRSetterOverloading") { map ->
		with (map) {
			put(
					DECL_SETTER_MUST_HAVE_ONE_PARAM,
					"Property setters may only take one parameter."
			)
			
			put(
					DECL_SETTER_MUST_RETURN_UNIT,
					"Setter functions must return Unit."
			)
			
			put(
					FirOverloadedSetterErrors.DECL_SETTER_TYPE_MATCHES_PROPERTY_TYPE,
					"Overloaded setter parameter type ''{0}'' shadows the type of the property ''{1}''.",
					RENDER_TYPE,
					RENDER_TYPE
			)
			
			put(
				FirOverloadedSetterErrors.PROP_NOT_MUTABLE,
				"''@HasCustomSetters'' can only be applied to mutable properties.",
			)
			
			put(
					FirOverloadedSetterErrors.CALLSITE_NO_MATCHING_SETTER,
					"No function matching ''set-{0}({1}): Unit'' found." +
					"Available setter types: {2}",
					STRING,
					RENDER_TYPE,
					RENDER_COLLECTION_OF_TYPES
			)
		}
	}
	
}