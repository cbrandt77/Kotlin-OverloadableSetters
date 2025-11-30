package cb77.lang.plugins.kt.overloadablesetters.util

import cb77.lang.plugins.kt.overloadablesetters.fir.setterOverloadFinderService
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.processAllDeclaredCallables
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.Name

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }
fun String.uncapitalize() = replaceFirstChar { if (it.isUpperCase()) it.lowercaseChar() else it }

/**
 * If a letter: if uppercase, returns lowercase version, and vice-versa.
 * If not a letter: returns self
 */
fun Char.swapCase(): Char {
	return when {
		!isLetter() -> this
		isLowerCase() -> this.uppercaseChar()
		else -> this.lowercaseChar()
	}
}

fun FirPropertySymbol.supportsCustomSetters(session: FirSession): Boolean {
	return session.setterOverloadFinderService.propertySupportsOverloadedSetters(this)
}

// no way to
fun FirClassSymbol<*>.getDeclaredAndInheritedCallables(session: FirSession, memberRequiredPhase: FirResolvePhase = FirResolvePhase.STATUS): Sequence<FirCallableSymbol<*>> {
	return this.getSuperTypes(session)
		.asSequence()
		.mapNotNull { it.toClassSymbol(session) }
		.flatMap { classSymbol ->
			mutableListOf<FirCallableSymbol<*>>().apply {
				classSymbol.processAllDeclaredCallables(session, memberRequiredPhase, processor=this::add)
			}// possible stdlib addition: sequence mapping with callback support?
		}
}

fun FirFunction.getReceiverClass(session: FirSession): FirRegularClassSymbol? {
	return when {
		this.isExtension -> this.receiverParameter?.typeRef?.toRegularClassSymbol(session)
		else -> this.getContainingClass()?.symbol
	}
}

fun findPropertyByName(owningClass: FirClassSymbol<*>, propertyName: String, session: FirSession): FirPropertySymbol? {
	val name = Name.identifier(propertyName)
	return owningClass.declaredProperties(session).find { it.name == name }
}