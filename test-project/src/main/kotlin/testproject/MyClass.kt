package testproject

import cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters

open class Parent {
	@HasCustomSetters
	open var parentProp: Int = 4
	
	open fun `set-parentProp`(value: String) {
	
	}
}

class Child : Parent() {
	override var parentProp: Int = 0
		get() = 5
	
	override fun `set-parentProp`(value: String) {
	
	}
}

fun Child.`set-parentProp`(value: Boolean) {
	this.parentProp = if (value) 1 else 0
}

class SetterTester {
	@HasCustomSetters
	var foo: String = ""
}

fun SetterTester.`set-foo`(value: Int) {
	this.foo = value.toString()
}

fun foo() {
	val x = SetterTester()
	x.foo = ""
	x.foo = 2
}

fun main() {

}
