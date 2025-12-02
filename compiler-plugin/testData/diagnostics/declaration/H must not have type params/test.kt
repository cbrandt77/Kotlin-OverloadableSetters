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
	
	fun <!SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS!><T><!> `set-someProperty`(value: Int) {
		someProperty = value.toString()
	}
	
	@HasCustomSetters
	var someOtherProperty: Int = 0
}

fun <!SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS!><T, U><!> MyClass.`set-someOtherProperty`(value: String) {
	someOtherProperty = java.lang.Integer.parseInt(value)
}
