package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.error3
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.psi.KtElement


object FirOverloadedSetterErrors : KtDiagnosticsContainer() {
	// declaration: (for function named `set-bar`, where `bar` is a valid property that's annotated to support custom setters)
	/**
	 * Setter returns something besides Unit
	 */
	val DECL_SETTER_MUST_RETURN_UNIT by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
	
	/**
	 * Setter has 0 or 2+ parameters
	 */
	val DECL_SETTER_MUST_HAVE_ONE_PARAM by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_SIGNATURE_OR_DEFAULT)
	
	/**
	 * Setter's parameter type shadows the original property setter
	 */
	val DECL_SETTER_TYPE_MATCHES_PROPERTY_TYPE by error2<KtElement, ConeKotlinType, ConeKotlinType>(SourceElementPositioningStrategies.DEFAULT)
	
	
	// annotated property
	/**
	 * Annotated property is a val
	 */
	val PROP_NOT_MUTABLE by error0<KtElement>()
	
	
	// callsite
	/**
	 * No setter found for "setBar(SomeType)", matching types = "SomeOtherType" or "OriginalPropertyType"
	 */
	val CALLSITE_NO_MATCHING_SETTER by error3<KtElement, String, ConeKotlinType, Collection<ConeKotlinType>>(SourceElementPositioningStrategies.OPERATOR)
	
	/**
	 * Setter isn't visible to the caller
	 */
	val CALLSITE_SETTER_NOT_ACCESSIBLE by error0<KtElement>(SourceElementPositioningStrategies.OPERATOR)
	
	
	/**
	 * Optional diagnostics for setter functions annotated with the @Setter annotation
	 */
	object SetterAnnot {
		/**
		 * A setter doesn't follow the pattern `set-{property}`
		 */
		val DECL_SETTER_INCORRECT_NAMING_SCHEME by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_NAME_ONLY)
		
		/**
		 * A setter is named `set-bar` but `bar` does not exist.
		 *
		 * Error param = name
		 */
		val DECL_SETTER_NO_PROP_FOUND_FOR_NAME by error1<KtElement, String>(SourceElementPositioningStrategies.DECLARATION_NAME_ONLY)
	}
	
	override fun getRendererFactory() = FirOverloadedSetterDefaultMessages
}

