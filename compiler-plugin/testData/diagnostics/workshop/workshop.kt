// RUN_PIPELINE_TILL: FRONTEND

// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;
annotation class HasCustomSetters

// FILE: foo.kt
package foo;

import annotations.HasCustomSetters

class MyClass {
	var someProperty: String = ""
	
	@Suppress("SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED")
	fun `set-someProperty`(value: Int) {
		someProperty = value.toString()
	}
}


// MODULE: main(lib)
// FILE: bar.kt
package bar;

import foo.MyClass

fun MyClass.foo() {
	this.someProperty = "foooo"
}

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.foo()
}
