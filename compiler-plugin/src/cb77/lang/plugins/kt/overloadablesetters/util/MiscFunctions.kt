package cb77.lang.plugins.kt.overloadablesetters.util

import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.Name

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