// RUN_PIPELINE_TILL: FRONTEND

// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;

annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters

class MyClass {
	@HasCustomSetters
	var someProperty: String = ""
}

fun MyClass.<!SETTER_DECL_TARGET_PROPERTY_NOT_FOUND!>`set-nonexistentProperty`<!>(value: Int) {
	someProperty = value.toString()
}


// MODULE: main(lib)
// FILE: bar.kt
package bar;

import foo.MyClass

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.someProperty = <!ARGUMENT_TYPE_MISMATCH!>2<!>
	x.<!UNRESOLVED_REFERENCE!>nonexistentProperty<!> = 2
}
