// RUN_PIPELINE_TILL: FRONTEND

// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;

annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters

open class MySuperClass {
	@HasCustomSetters
	private var someProperty: String = ""
}

class MyClass : MySuperClass() {
	private fun <!SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE!>`set-someProperty`<!>(value: Int) {
		someProperty = value.toString()
	}
}


// MODULE: main(lib)
// FILE: bar.kt
package bar;

import foo.MyClass

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.someProperty = <!ASSIGNMENT_TYPE_MISMATCH!>2<!>
}
