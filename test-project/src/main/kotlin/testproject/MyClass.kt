package testproject

import cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters

open class Parent {
	@HasCustomSetters
	open var parentProp: Int = 4
}

class Child : Parent() {
	override var parentProp: Int = 0
		get() = 5
}

fun Child.`set-parentProp`(value: Boolean) {
	this.parentProp = if (value) 1 else 0
}

class SetterTester {
	@HasCustomSetters
	var foo: String = ""
	
	fun x() {
	
	}
}

fun SetterTester.`set-foo`(value: Int) {
	this.foo = value.toString()
}

fun foo() {
	val x = SetterTester()
	x.`set-foo`("")
}

fun main() {

}
