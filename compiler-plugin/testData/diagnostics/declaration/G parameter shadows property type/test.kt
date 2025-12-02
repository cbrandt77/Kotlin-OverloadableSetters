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
	
	fun <!SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE!>`set-someProperty`<!>(value: String) {
		<!OVERLOAD_RESOLUTION_AMBIGUITY!>someProperty<!> = value.toString()
	}
	
	@HasCustomSetters
	var someOtherProperty: Int = 0
}

fun MyClass.<!SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE!>`set-someOtherProperty`<!>(value: Int) {
	someOtherProperty = value
}
