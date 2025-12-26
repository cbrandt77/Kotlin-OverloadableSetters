package cb77.lang.plugins.kt.overloadablesetters.util

import cb77.lang.plugins.kt.overloadablesetters.fir.setterOverloadFinderService
import dev.zacsweers.metro.compiler.compat.ext.toRegularClassSymbol
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.processAllDeclaredCallables
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.Name


fun FirClassSymbol<*>.getDeclaredAndInheritedCallables(session: FirSession, memberRequiredPhase: FirResolvePhase = FirResolvePhase.STATUS): Sequence<FirCallableSymbol<*>> {
	val superTypeSequence = this.getSuperTypes(session)
		.asSequence()
		.mapNotNull { it.toClassSymbol(session) }
	
	return (sequenceOf(this) + superTypeSequence)
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
