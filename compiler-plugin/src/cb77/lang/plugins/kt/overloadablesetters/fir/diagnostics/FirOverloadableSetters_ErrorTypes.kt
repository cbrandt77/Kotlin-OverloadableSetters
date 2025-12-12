package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.error3
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty


object FirOverloadableSetters_ErrorTypes : KtDiagnosticsContainer() {
	//region Setter Function Declarations
	
	// WARNINGS: (Applies to everything)
	/**
	 * For a function `set-bar`: the property "`bar`" doesn't exist in the scope.
	 *
	 * Should be suppressable, since it might apply to functions that aren't supposed to be overloaded setters.
	 *
	 * @param _1 The name of what would be the target property, from the name of the function.
	 */
	@Suppress("KDocUnresolvedReference")
	val SETTER_DECL_TARGET_PROPERTY_NOT_FOUND by warning1<KtNamedFunction, String>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	/**
	 * For a function `set-bar`: the property "`bar`" exists in the scope, but is not marked as supporting custom setters.
	 *
	 * Should be suppressable, since it might apply to functions that aren't supposed to be overloaded setters.
	 *
	 * @param _1 The target property the setter function's name invokes.
	 */
	@Suppress("KDocUnresolvedReference")
	val SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED by warning1<KtNamedFunction, FirPropertySymbol>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	
	// ERRORS: (Applies only to functions we know are intended to be overloaded property setters)
	/**
	 * For a function `MyClass#set-bar`, `MyClass#bar` is not visible to the declaration.
	 *
	 * This is mainly a sanity check, not a restriction.  If it's not visible to the setter function, then the setter function can't do its job.
	 *
	 * @param _1 The target property the setter function's name invokes.
	 */
	@Suppress("KDocUnresolvedReference")
	val SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE by error1<KtNamedFunction, FirPropertySymbol>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	/**
	 * For a setter function `MyClass#set-bar`, `set-bar` has wider visibility than `bar`.
	 *
	 * This is an issue because the visibility system is baked into the IDEs' autocomplete systems,
	 * so referencing otherwise-invisible properties will make them freak out.
	 *
	 * Note that a similar thing of referencing a private setter _should_ be allowed, because that's an intended use-case.
	 * But also if the setter isn't accessible to the caller, that's a different IDE lint.
	 * (Of course, I don't know how well the IDE will react to trying to "invoke" a private setter from outside the class, but we'll see when we get there.)
	 * TODO
	 */
	val SETTER_DECL_CANNOT_WIDEN_VISIBILITY by error3<KtNamedFunction, Visibility, FirCallableSymbol<*>, Name>(SourceElementPositioningStrategies.DECLARATION_NAME)
	
	
	/**
	 * A setter function has more than one value parameter.
	 *
	 * Setters must take one parameter, no more and no less. Otherwise the variable assignment is screwy.
	 *
	 * Sidenote: I wonder how optional args might work with this?
	 * I feel like they could potentially be supported by the language, but then how would you actually implement that in a standard property setter?
	 * Pretty sure it would work if you could directly invoke the method, but with Kotlin's hidden backing methods, that won't work.
	 */
	val SETTER_DECL_MUST_HAVE_SINGLE_PARAM by error0<KtNamedFunction>(SourceElementPositioningStrategies.PARAMETERS_WITH_DEFAULT_VALUE)
	
	/**
	 * A setter function's input parameter is the same as the property it's setting, so it would "shadow" it.
	 *
	 * This is a warning because having two functions for the default case would make function call resolution kinda screwy.
	 *
	 * @param _1 The setter's type
	 * @param _2 The property's type
	 */
	@Suppress("KDocUnresolvedReference")
	val SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE by error2<KtParameter, ConeKotlinType, ConeKotlinType>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
	
	/**
	 * A setter function has type parameters.
	 *
	 * I feel like implied generics _could_ work, but how on earth would you forcibly disambiguate those??
	 * I don't want to deal with that, so we're not supporting it.
	 */
	val SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS by error0<KtNamedFunction>(SourceElementPositioningStrategies.TYPE_PARAMETERS_LIST)
	
	/**
	 * A setter function has context parameters.
	 *
	 * Honestly I feel like this _could_ work; because they're implied, we wouldn't need to find a way to shove them into the assignment operator.
	 * Might revisit this later.
	 */
	val SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS by error0<KtNamedFunction>(SourceElementPositioningStrategies.DECLARATION_SIGNATURE_OR_DEFAULT)
	
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
	
	/**
	 * "HasCustomSetters" annotation is only applicable to `var`s, not `val`s.
	 */
	val SETTER_ANNOTATED_PROP_INVALID by error1<KtProperty, String>(SourceElementPositioningStrategies.DEFAULT)
	
	//endregion Property Declaration
	
	
	override fun getRendererFactory() = FirOverloadableSetters_ErrorMessages
}

