package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.TO_STRING
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.VISIBILITY
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.DECLARATION_NAME
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers.RENDER_TYPE
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors.CANNOT_WEAKEN_ACCESS_PRIVILEGE

object FirOverloadableSetters_ErrorMessages : BaseDiagnosticRendererFactory() {
	override val MAP by KtDiagnosticFactoryToRendererMap("FIRSetterOverloading") { map ->
		with (map) {
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_HAVE_SINGLE_PARAM,
					"Property setters must have exactly one parameter."
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_RETURN_UNIT,
					"Property setters must return Unit."
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE,
					"Overloaded setter parameter type ''{0}'' shadows the type of the property ''{1}''.",
					RENDER_TYPE,
					RENDER_TYPE
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_ANNOTATED_PROP_NOT_MUTABLE,
					"A 'val' property cannot have a setter.",
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE,
					"Setter target ''{0}'' is not visible in this scope.",
					FirDiagnosticRenderers.SYMBOL_WITH_CONTAINING_DECLARATION
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS,
					"Property setters do not support type parameters."
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS,
					"Property setters do not support context parameters. " +
					"This may change in the future."
			)
			
			put(
					FirOverloadableSetters_ErrorTypes.SETTER_DECL_CANNOT_WIDEN_VISIBILITY,
					"Cannot weaken access privilege {0} for ''{1}'' in ''{2}''.",
					VISIBILITY,
					DECLARATION_NAME,
					TO_STRING
			)
		}
	}
	
}