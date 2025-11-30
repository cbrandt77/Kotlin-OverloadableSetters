package cb77.lang.plugins.kt.overloadablesetters.util

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.analysis.checkers.isVisibleInClass
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.packageName

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


fun FirBasedSymbol<*>.isVisibleFrom(origin: FirBasedSymbol<*>): Boolean {
	val status = when (this) {
		is FirCallableSymbol<*> -> resolvedStatus
		is FirClassLikeSymbol -> resolvedStatus
		else -> return true
	}
	return isVisibleFrom(origin, status)
}
/**
 * Returns true if this symbol can be accessed by the argument.
 */
fun FirBasedSymbol<*>.isVisibleFrom(origin: FirBasedSymbol<*>, status: FirDeclarationStatus): Boolean {
	val classPackage = origin.packageFqName()
	val packageName = when (this) {
		is FirCallableSymbol<*> -> callableId.packageName
		is FirClassLikeSymbol<*> -> classId.packageFqName
		else -> return true
	}
	val visibility = status.visibility
	if (visibility == Visibilities.Private || !visibility.visibleFromPackage(classPackage, packageName)) return false
	if (visibility == Visibilities.Internal) {
		val containingClassModuleData = origin.moduleData
		return when (moduleData) {
			containingClassModuleData -> true
			in containingClassModuleData.friendDependencies -> true
			in containingClassModuleData.dependsOnDependencies -> true
			else -> false
		}
	}
	return true
}