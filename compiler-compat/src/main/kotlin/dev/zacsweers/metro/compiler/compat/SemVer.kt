package dev.zacsweers.metro.compiler.compat

/**
 * A janky homemade semver parser with comparison
 *
 * This, no joke, was harder to understand and reason out than the rest of the compiler API.
 * There are just so many moving parts in versioning...
 */
class SemVer(stringRep: String) : Comparable<SemVer> {
	val version: IntArray // 0-4 are standard, but since they're all compared the same way, it doesn't really matter how many we have
	
	val suffix: Array<Suffix>
	
	val isRelease: Boolean
		get() = suffix.isEmpty()
	
	init {
		val split = stringRep.split('-').toTypedArray() // why can't I just invoke the standard Java method? I like arrays!
		
		suffix = Array(split.size - 1) { i ->
			Suffix(split[(i + 1)])
		}
		
		val versionPart = split[0].removePrefix("v").split('.')
		version = IntArray(versionPart.size) { versionPart[it].toIntOrNull() ?: 0 }
	}
	
	override operator fun compareTo(other: SemVer): Int {
		val majorCompare = compareVersionNoSuffix(other)
		if (majorCompare != 0)
			return majorCompare;
		
		return compareSuffixes(other) ?: 0
	}
	
	fun compareVersionNoSuffix(other: SemVer): Int {
		val finalVersionIndex = maxOf(this.version.size, other.version.size)
		for (i in 0..<finalVersionIndex) {
			val ourSubV = if (i < this.version.size) this.version[i] else 0
			val theirSubV = if (i < other.version.size) other.version[i] else 0
			
			if (ourSubV != theirSubV)
				return (ourSubV - theirSubV) * (finalVersionIndex - i) // weight major versions more harshly
		}
		return 0
	}
	
	fun compareSuffixes(other: SemVer): Int? {
		// homemade "full length zip with defaults"
		for (i in 0..<maxOf(this.suffix.size, other.suffix.size)) {
			val fromThis = this.suffix.getOrElse(i) { Suffix.EMPTY }
			val fromOther = other.suffix.getOrElse(i) { Suffix.EMPTY }
			
			val comparison = fromThis.compareTo(fromOther)
			if (comparison != 0)
				return comparison;
		}
		
		return 0;
	}
	
	override fun equals(other: Any?): Boolean {
		return other is SemVer
		       && this.version.contentEquals(other.version)
		       && this.suffix.contentEquals(other.suffix)
	}
	
	override fun hashCode(): Int {
		var result = version.contentHashCode()
		result = 31 * result + suffix.contentHashCode()
		return result
	}
	
	data class Suffix(val prefix: String, val number: Int) {
		val isEmpty: Boolean
			get() = prefix.isEmpty() && number == 0
		
		val hasWord: Boolean
			get() = !prefix.isEmpty()
		
		val positionRelativeToNoPrefix = getPrefixPositioningRelativeToStable(prefix)
		
		
		
		/**
		 * Returns null if the two are different downstream branches, and therefore cannot be compared.
		 *
		 * Else returns some "difference", where this being greater than the other is positive, equal to is 0, and lesser than is negative.
		 *
		 * The numbers themselves depend on the type of suffix; just the sign is important.
		 */
		fun compareTo(other: Suffix): Int? {
			// we can only compare their numbers if they have the same prefix, else the numbers are meaningless
			if (this.prefix == other.prefix) // also compares raw numbers, raw words, and both being blank
				return this.number - other.number;
			
			/*
			Version types:
			2.2.20-ij252-24
			2.2.20-dev-5437
			2.2.20
			2.2.20-beta1
			 */
			// now we just need to ensure they CAN BE COMPARED AT ALL
			if (this.positionRelativeToNoPrefix > 0 && other.positionRelativeToNoPrefix > 0) {
				// prefixes aren't equal but both are downstream -> incompatible
				return null;
			}
			
			// then it's just "worse are worse better are better"
			return this.positionRelativeToNoPrefix - other.positionRelativeToNoPrefix
		}
		
		
		
		companion object {
			private val wordNumberRegex = Regex("^([a-zA-Z]+)?(\\d+)?$")
			
			/**
			 * Compiler version prefixes that are _greater than_ their annotated version.
			 * All of these are incompatible with any other prefix
			 */
			private val prefix_Ahead = arrayOf(
					"ij",
					"dev"
			)
			
			/**
			 * Compiler version prefixes that are _lesser than_ their annotated version.
			 *
			 * For example, `2.2.20-ij252-24` is ahead of `2.2.20`, while `2.2.20-beta1` is _behind_ it.
			 */
			private val prefix_Behind = arrayOf(
					"beta",
					"alpha"
			)
			
			val EMPTY = Suffix("", 0)
			
			/**
			 * If positive, indicates that the prefix means it's ahead of its annotated version
			 * If negative, indicates that the prefix is behind its annotated version
			 * If 0, indicates not applicable
			 */
			fun getPrefixPositioningRelativeToStable(prefix: String?): Int {
				if (prefix == null)
					return 0
				val inAhead = prefix_Ahead.indexOf(prefix)
				if (inAhead != -1)
					return inAhead
				val inBehind = prefix_Behind.indexOf(prefix)
				if (inBehind != -1)
					return -inBehind
				return 0
			}
			
			/**
			 * Construct the right suffix subclass by matching the string against different patterns:
			 * - "^{word}{number}$" -> WordNumberSuffix
			 * - "^{number}$" -> NumberOnlySuffix
			 * - else -> WordOnlySuffix
			 */
			operator fun invoke(stringRep: String): Suffix {
				if (stringRep.isBlank())
					return EMPTY;
				
				wordNumberRegex.matchEntire(stringRep)?.groupValues?.let { groups ->
					return Suffix(groups[1], groups[2].toIntOrNull() ?: 0)
				}
				return Suffix(stringRep, 0)
			}
		}
	}
	
	
}