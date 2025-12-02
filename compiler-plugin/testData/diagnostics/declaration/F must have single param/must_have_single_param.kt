// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
// FILE: HasCustomSetters.kt
package annotations;

annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters

class MyClass {
	@HasCustomSetters
	var someProperty: String = ""
	
	private fun `set-someProperty`<!SETTER_DECL_MUST_HAVE_SINGLE_PARAM!>(value: Int, otherParam: String)<!> {
		someProperty = value.toString()
	}
	
	// should work
	fun `set-someProperty`(value: Char) {
		someProperty = value.toString()
	}
}

fun MyClass.`set-someProperty`<!SETTER_DECL_MUST_HAVE_SINGLE_PARAM!>(value: String, otherParam: Any)<!> {
	someProperty = value
}

fun MyClass.`set-someProperty`<!SETTER_DECL_MUST_HAVE_SINGLE_PARAM!>()<!> {
	someProperty = 'c'
}
