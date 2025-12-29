package io.github.cbrandt77.kt.overloadablesetters

object OverloadedSetterOptions {
	var allowAmbiguousCasePropertyNames: Boolean = false
	
	/**
	 * Keep the setterNameRegex from being overwritten again if the --pattern=whatever opt is set
	 */
	var lockRegexOption: Boolean = false
	
	var setterNameRegex: Regex = Regex("set-(\\w+)")
		set(value) {
			if (lockRegexOption)
				return;
			field = value
		}
}