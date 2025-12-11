package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.checkers.declaration.function

import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
import cb77.lang.plugins.kt.overloadablesetters.util.findPropertyByName
import cb77.lang.plugins.kt.overloadablesetters.util.getPropertyNameFromSetterName
import cb77.lang.plugins.kt.overloadablesetters.util.getReceiverClass
import cb77.lang.plugins.kt.overloadablesetters.util.isVisibleFrom
import cb77.lang.plugins.kt.overloadablesetters.util.supportsCustomSetters
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.utils.exceptions.withFirEntry
import org.jetbrains.kotlin.utils.exceptions.errorWithAttachment

object FirChecker_SetterFunctionLinter : FirFunctionChecker(MppCheckerKind.Common) {
	context(ctx: CheckerContext, reporter: DiagnosticReporter)
	override fun check(declaration: FirFunction) {
		if (declaration.origin != FirDeclarationOrigin.Source)
			return;
		
		// this is our primary guard: only act on functions of the form `set-bar`
		val referencedPropertyName = getPropertyNameFromSetterName(declaration.nameOrSpecialName)
		                             ?: return;
		
		// if it's not a method (extension or otherwise), don't take it
		val owningClass: FirClassSymbol<*> = declaration.getReceiverClass(ctx.session)
		                                     ?: return;
		
		val referencedProperty: FirPropertySymbol = findPropertyByName(owningClass, referencedPropertyName, ctx.session).toList().let { allFoo ->
			if (allFoo.isEmpty()) {
				reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_TARGET_PROPERTY_NOT_FOUND, referencedPropertyName)
				return;
			}
			
			val supportedFoo = allFoo.filter { it.supportsCustomSetters(ctx.session) }
			if (supportedFoo.isEmpty()) {
				reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED, allFoo[0])
				return;
			}
			
			supportedFoo[0] // TODO figure out if ambiguous property reference is even a problem
		}
		
		// Now we're only acting on functions that are DEFINITELY meant to be our custom setters
		
		when {
			!referencedProperty.isVisibleFrom(declaration.symbol, ctx.session) -> {
				reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE, referencedProperty)
			}
			declaration.visibility > referencedProperty.visibility -> {
				reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_CANNOT_WIDEN_VISIBILITY, referencedProperty.visibility, referencedProperty, owningClass.name)
			}
		}
		
		// PARAMETERS:
		if (declaration.valueParameters.size != 1) {
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_HAVE_SINGLE_PARAM)
		} else {
			val firstParameter = declaration.valueParameters[0]
			val firstParamType = firstParameter.returnTypeRef.coneTypeOrNull
			val propertyType = referencedProperty.resolvedReturnType
			
			if (firstParamType == propertyType) {
				reporter.reportOn(firstParameter.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE, firstParamType, propertyType)
			}
		}
		
		if (declaration.typeParameters.isNotEmpty()) {
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS)
		}
		if (declaration.contextParameters.isNotEmpty()) {
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS)
		}
		
		// RETURN TYPE:
		if (!declaration.returnTypeRef.coneType.isUnit) {
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_DECL_MUST_RETURN_UNIT)
		}
	}
	
	private operator fun Visibility.compareTo(other: Visibility): Int {
		return this.compareTo(other) ?: 0
	}
}