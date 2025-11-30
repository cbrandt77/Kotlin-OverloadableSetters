package cb77.lang.plugins.kt.overloadablesetters.util

import cb77.lang.plugins.kt.overloadablesetters.fir.setterOverloadFinderService
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.getContainingSymbol
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.processAllDeclaredCallables
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.scopes.collectAllProperties
import org.jetbrains.kotlin.fir.scopes.impl.FirPackageMemberScope
import org.jetbrains.kotlin.fir.scopes.impl.PACKAGE_MEMBER
import org.jetbrains.kotlin.fir.scopes.impl.declaredMemberScope
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFileSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.name.Name


fun FirPropertySymbol.supportsCustomSetters(session: FirSession): Boolean {
	return session.setterOverloadFinderService.propertySupportsOverloadedSetters(this)
}

fun FirClassSymbol<*>.getDeclaredAndInheritedCallables(session: FirSession, memberRequiredPhase: FirResolvePhase = FirResolvePhase.STATUS): Sequence<FirCallableSymbol<*>> {
	return this.getSuperTypes(session)
		.asSequence()
		.mapNotNull { it.toClassSymbol(session) }
		.flatMap { classSymbol ->
			mutableListOf<FirCallableSymbol<*>>().apply {
				classSymbol.processAllDeclaredCallables(session, memberRequiredPhase, processor=this::add)
			}// possible stdlib addition: sequence flat-mapping with callback support?
		}
}

fun FirFunction.getReceiverClass(session: FirSession): FirRegularClassSymbol? {
	return when {
		this.isExtension -> this.receiverParameter?.typeRef?.toRegularClassSymbol(session)
		else -> this.getContainingClass()?.symbol
	}
}

fun findPropertyByName(owningClass: FirClassSymbol<*>, propertyName: String, session: FirSession): Sequence<FirPropertySymbol> {
	val name = Name.identifier(propertyName)
	return owningClass.propertiesInScope(session).filter { it.name == name }
}

// TODO turn this into a scope search instead of a direct "check the class", both so we get the extension properties AND the supertype properties
fun FirClassSymbol<*>.propertiesInScope(session: FirSession, memberRequiredPhase: FirResolvePhase = FirResolvePhase.STATUS): Sequence<FirPropertySymbol> {
	return this.getDeclaredAndInheritedCallables(session, memberRequiredPhase)
		.filterIsInstance<FirPropertySymbol>()
}
