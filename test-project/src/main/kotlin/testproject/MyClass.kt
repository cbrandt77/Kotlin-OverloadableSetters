package testproject

import cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters

class SetterTester {
	@HasCustomSetters
	var foo: String = ""
	
	fun `set-foo`(value:) {
	
	}
}
