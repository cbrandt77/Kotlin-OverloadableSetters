package testproject

import cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters

open class Parent {

}

class Child {

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
