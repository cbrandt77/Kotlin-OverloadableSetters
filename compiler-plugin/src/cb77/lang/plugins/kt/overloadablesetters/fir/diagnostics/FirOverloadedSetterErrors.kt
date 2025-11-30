package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.annotations.ApiStatus
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.error3
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.psi.KtContextReceiverList
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.stubs.elements.KtContextReceiverElementType


object FirOverloadedSetterErrors : KtDiagnosticsContainer() {
	//region Setter Function Declarations
	
	// WARNINGS: (Applies to everything)
	/**
	 * For a function `set-bar`: the property "`bar`" doesn't exist in the scope.
	 *
	 * Should be suppressable, since it might apply to functions that aren't supposed to be overloaded setters.
	 */
	val SETTER_DECL_TARGET_PROPERTY_NOT_FOUND by warning1<KtNamedFunction, String>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	/**
	 * For a function `set-bar`: the property "`bar`" exists in the scope, but is not marked as supporting custom setters.
	 *
	 * Should be suppressable, since it might apply to functions that aren't supposed to be overloaded setters.
	 */
	val SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED by warning1<KtNamedFunction, FirPropertySymbol>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	
	// ERRORS: (Applies only to functions we know are intended to be overloaded property setters)
	/**
	 * A setter function has more than one value parameter.
	 *
	 * Setters must take one parameter, no more and no less. Otherwise the variable assignment is screwy.
	 *
	 * Sidenote: I wonder how optional args might work with this?
	 * I feel like they could potentially be supported by the language, but then how would you actually implement that in a standard property setter?
	 * Pretty sure it would work if you could directly invoke the method, but with Kotlin's hidden backing methods, that won't work.
	 */
	val SETTER_DECL_MUST_HAVE_SINGLE_PARAM by error0<KtNamedFunction>(SourceElementPositioningStrategies.DECLARATION_SIGNATURE_OR_DEFAULT)
	
	/**
	 * A setter function's input parameter is the same as the property it's setting, so it would "shadow" it.
	 *
	 * This is a warning because having two functions for the default case would make function call resolution kinda screwy.
	 */
	val SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE by error2<KtParameter, ConeKotlinType, ConeKotlinType>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
	
	/**
	 * A setter function has type parameters.
	 *
	 * I feel like implied generics _could_ work, but how on earth would you forcibly disambiguate those??
	 * I don't want to deal with that, so we're not supporting it.
	 */
	val SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS by error0<KtTypeArgumentList>(SourceElementPositioningStrategies.DECLARATION_SIGNATURE_OR_DEFAULT)
	
	/**
	 * A setter function has context parameters.
	 *
	 * Honestly I feel like this _could_ work; because they're implied, we wouldn't need to find a way to shove them into the assignment operator.
	 * Might revisit this later.
	 */
	val SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS by error0<KtContextReceiverList>(SourceElementPositioningStrategies.DECLARATION_SIGNATURE_OR_DEFAULT)
	
	/**
	 * A setter function has a return type that isn't unit.
	 *
	 * This is for compatibility with standard Kotlin setter logic.
	 */
	val SETTER_DECL_MUST_RETURN_UNIT by error0<KtNamedFunction>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
	//endregion Setter Function Declarations
	
	//region Property Declaration
	
	/**
	 * "HasCustomSetters" annotation is only applicable to `var`s, not `val`s.
	 */
	val SETTER_ANNOTATED_PROP_NOT_MUTABLE by error0<KtProperty>(SourceElementPositioningStrategies.VAL_OR_VAR_NODE)
	
	//endregion Property Declaration
	
	
	override fun getRendererFactory() = FirOverloadedSetterDefaultMessages
}

