package cb77.lang.plugins.kt.overloadablesetters.util

import dev.zacsweers.metro.compiler.compat.CompatContext.Companion.getContainingClassSymbol
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.resolve.providers.firProvider
import org.jetbrains.kotlin.fir.resolve.providers.getContainingFile
import org.jetbrains.kotlin.fir.resolve.toClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.utils.exceptions.withFirSymbolEntry
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.exceptions.errorWithAttachment

// putting these in one place so if I completely misunderstood how names work I can fix it easily
fun makeSetterName(property: FirProperty): String {
	return makeSetterName(property.name)
}

fun makeSetterName(property: FirPropertySymbol): String {
	return makeSetterName(property.name)
}

fun makeSetterName(propertyName: Name): String {
	return makeSetterName(propertyName.asString())
}

fun makeSetterName(propertyName: String): String {
	return "set-${propertyName}"
}

/**
 * Matches the setter format. Group $1 = raw name of the target property
 */
private val SETTER_REGEX = Regex("set-(\\w+)")

fun getPropertyNameFromSetterName(setterName: Name): String? {
	return getPropertyNameFromSetterName(setterName.asString())
}

fun getPropertyNameFromSetterName(setterName: String): String? {
	return SETTER_REGEX.matchEntire(setterName)?.let { it.groups[1]?.value }
}

fun FirBasedSymbol<*>.isVisibleFrom(origin: FirBasedSymbol<*>, session: FirSession): Boolean {
	val status = when (this) {
		is FirCallableSymbol<*> -> resolvedStatus
		is FirClassLikeSymbol -> resolvedStatus
		else -> return true
	}
	return isVisibleFrom(origin, status, session)
}

/**
 * Returns true if this symbol can be accessed by the argument.
 */
fun FirBasedSymbol<*>.isVisibleFrom(attemptedInvoker: FirBasedSymbol<*>, status: FirDeclarationStatus, session: FirSession): Boolean {
//	fun FirBasedSymbol<*>.getPackageName() = when (this) {
//		is FirCallableSymbol<*> -> callableId.packageName
//		is FirClassLikeSymbol<*> -> classId.packageFqName
//		else -> null
//	}
	
	val ourVisibility = status.visibility
	if (ourVisibility == Visibilities.Public)
		return true;
	
	// For any non-public visibility:
	run {
		when (val ourContainingClass = this.getContainingClassSymbol()) {
			// If we're on the top level and if the invoker is declared in the same file, it's always "true", even if it's private
			null -> {
				val ourTopLevelContainer = session.firProvider.getContainingFile(this)
				                           ?: errorWithAttachment("No containing file found for `this` based symbol") {
					                           withFirSymbolEntry("this", this@isVisibleFrom)
				                           }
				
				val theirTopLevelContainer = session.firProvider.getContainingFile(attemptedInvoker);
				if (ourTopLevelContainer.symbol == theirTopLevelContainer?.symbol)
					return true;
			}
			// If we're in a class, and the invoker is either in the same class or in an outer class of this, it's always "true", even if the member is private
			else -> {
				val theirContainingClass = attemptedInvoker.getContainingClassSymbol() ?: return@run
				
				if (ourContainingClass == theirContainingClass || ourContainingClass.isDeclaredInInnerClassOf(theirContainingClass))
					return true;
			}
		}
	}

//	if (!ourVisibility.visibleFromPackage(attemptedInvoker.packageFqName(), getPackageName() ?: return true))
//		return false;
	
	return when (ourVisibility) {
		// If "Private", and the above conditions weren't satisfied, it's always "false".
		Visibilities.Private, Visibilities.PrivateToThis -> false
		// If "Internal", we only have to check if they're in the same module.
		Visibilities.Internal -> attemptedInvoker.moduleData.let { containingClassModuleData ->
			when (moduleData) {
				containingClassModuleData -> true
				in containingClassModuleData.friendDependencies -> true
				in containingClassModuleData.dependsOnDependencies -> true
				else -> false
			}
		}
		// If "Protected", we only have to check if we're in a superclass of the invoker's declared class.
		Visibilities.Protected -> attemptedInvoker.isDeclaredInSubClassOf(this, session)
		else -> false
	}
}


/**
 * Check if `declaredInOuter` can see `this`'s private members. (no jokes)
 * ```
 * class Foo {
 *   class Bar {
 *      private val inBar // this
 *   }
 *
 *   fun inFoo() { // declaredInOuter
 *      // I want to check if I can reference `Bar#inBar` from here
 *   }
 * }
 * ```
 */
fun FirBasedSymbol<*>.isDeclaredInInnerClassOf(declaredInOuter: FirBasedSymbol<*>): Boolean {
	if (declaredInOuter.packageFqName() != this.packageFqName())
		return false;
	
	// if neither are declared in classes, then it can't be in a parent
	var ourPossiblyInnerContainer = this.containingClassOrSelf() ?: return false;
	
	val theirPossiblyOuterContainerId = declaredInOuter.containingClassOrSelf()?.classId ?: return false;
	
	while (true) {
		if (ourPossiblyInnerContainer.classId == theirPossiblyOuterContainerId)
			return true;
		
		ourPossiblyInnerContainer = ourPossiblyInnerContainer.getContainingClassSymbol() ?: return false;
	}
}

/**
 * Returns false if:
 * - `declaredInSuper` and `this` are not either a) a class or b) declared inside a class
 * - `this` is not either a subclass of or declared inside a subclass of `declaredInSuper`'s declaring class (or itself if declaredInSuper IS a class)
 *
 * Else returns true
 */
fun FirBasedSymbol<*>.isDeclaredInSubClassOf(declaredInSuper: FirBasedSymbol<*>, session: FirSession): Boolean {
	val superContainingClass = declaredInSuper.containingClassOrSelf()?.classId ?: return false
	
	val ourContainingClass: FirClassLikeSymbol<*> = this.containingClassOrSelf() ?: return false
	
	return ourContainingClass.getSuperTypes(session, true).any { it.toClassLikeSymbol(session)?.classId == superContainingClass }
}

/**
 * Returns `this` if `this` is a FirClassLikeSymbol, or else attempts to get the containing class symbol of `this`
 */
fun FirBasedSymbol<*>.containingClassOrSelf(): FirClassLikeSymbol<*>? {
	return when (this) {
		is FirClassLikeSymbol<*> -> this
		else -> this.getContainingClassSymbol()
	}
}
