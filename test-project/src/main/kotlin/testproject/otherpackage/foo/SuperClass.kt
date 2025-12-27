package testproject.otherpackage.foo

import cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters

abstract class SuperClass {
	@HasCustomSetters
	abstract var superProp: Any

	fun `set-superProp`(el: Int) {

	}
}

fun foo() {
	val x: SuperClass = object : SuperClass() {
		@HasCustomSetters
		override var superProp: Any
			get() = TODO("Not yet implemented")
			set(value) {}
	}


}