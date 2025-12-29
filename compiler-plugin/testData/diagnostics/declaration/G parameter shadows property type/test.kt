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
	
	fun `set-someProperty`(value: <!SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE!>String<!>) {
		<!OVERLOAD_RESOLUTION_AMBIGUITY!>someProperty<!> = value.toString()
	}
	
	@HasCustomSetters
	var someOtherProperty: Int = 0
}

fun MyClass.<!EXTENSION_SHADOWED_BY_MEMBER!>`set-someOtherProperty`<!>(value: <!SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE!>Int<!>) {
	someOtherProperty = value
}
