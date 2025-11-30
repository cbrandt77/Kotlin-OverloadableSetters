package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import cb77.lang.plugins.kt.overloadablesetters.fir.setterOverloadFinderService
import cb77.lang.plugins.kt.overloadablesetters.util.getPropertyNameFromSetterName
import cb77.lang.plugins.kt.overloadablesetters.util.getReceiverClass
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.utils.exceptions.withFirEntry
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.exceptions.errorWithAttachment

object SetterFunctionDeclarationChecker : FirFunctionChecker(MppCheckerKind.Common) {
	context(ctx: CheckerContext, reporter: DiagnosticReporter)
	override fun check(declaration: FirFunction) {
		if (declaration.origin != FirDeclarationOrigin.Source)
			return;
		
		// this is our primary guard: only act on functions of the form `set-bar`
		val referencedPropertyName = getPropertyNameFromSetterName(declaration.nameOrSpecialName)
		                             ?: return;
		
		val owningClass: FirClassSymbol<*> = declaration.getReceiverClass(ctx.session)
		                                     ?: errorWithAttachment("No owning class found for function") {
												 withFirEntry("function", declaration)
											 }
		
		val referencedProperty: FirPropertySymbol = findPropertyByName(owningClass, referencedPropertyName).let {
			when {
				// These should be suppressable in case it's acting on an unintended place, so we short-circuit here
				it == null -> {
					reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_TARGET_PROPERTY_NOT_FOUND, referencedPropertyName)
					return;
				}
				(!ctx.session.setterOverloadFinderService.propertySupportsOverloadedSetters(it)) -> {
					reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED, it)
					return;
				}
				else -> it
			} // idk why doing these guards as "if/elif" statements doesn't smart-cast `referencedProperty` to non-nullable when they both return
		}
		
		"".toInt
		
		
		
		
		// Now we're only acting on functions that are DEFINITELY meant to be our custom setters
		
		// PARAMETERS:
		if (declaration.valueParameters.size != 1) {
			reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_MUST_HAVE_SINGLE_PARAM)
		} else {
			val firstParamType = declaration.valueParameters[0].returnTypeRef.coneTypeOrNull
			val propertyType = referencedProperty.resolvedReturnType
			
			if (firstParamType == propertyType) {
				reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE, firstParamType, propertyType)
			}
		}
		
		if (declaration.typeParameters.isNotEmpty()) {
			reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS)
		}
		if (declaration.contextParameters.isNotEmpty()) {
			reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS)
		}
		
		// RETURN TYPE:
		if (!declaration.returnTypeRef.coneType.isUnit) {
			reporter.reportOn(declaration.source, FirOverloadedSetterErrors.SETTER_DECL_MUST_RETURN_UNIT)
		}
	}
	
	context(ctx: CheckerContext) // TODO change to full scope search to find extension properties
	fun findPropertyByName(owningClass: FirClassSymbol<*>, propertyName: String): FirPropertySymbol? {
		val name = Name.identifier(propertyName)
		return owningClass.declaredProperties(ctx.session).find { it.name == name }
	}
}