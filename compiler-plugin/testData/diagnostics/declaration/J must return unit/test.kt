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
	
	fun `set-someProperty`(value: Int): <!SETTER_DECL_MUST_RETURN_UNIT!>String<!> {
		someProperty = value.toString()
		return "Whoops!"
	}
	
	@HasCustomSetters
	var someOtherProperty: Char = 'c'
}

fun MyClass.`set-someOtherProperty`(value: Int): <!SETTER_DECL_MUST_RETURN_UNIT!>Any?<!> {
	someOtherProperty = value.toChar()
	return null
}
