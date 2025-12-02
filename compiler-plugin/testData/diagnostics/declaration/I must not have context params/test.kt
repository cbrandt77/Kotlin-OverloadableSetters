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
	
	<!SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS!><!UNSUPPORTED_FEATURE!>context(someContext: Any)<!>
	fun `set-someProperty`(value: Int)<!> {
		someProperty = value.toString()
	}
	
	@HasCustomSetters
	var someOtherProperty: Char = 'c'
}

<!SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS!><!UNSUPPORTED_FEATURE!>context(someContext: Any)<!>
fun MyClass.`set-someOtherProperty`(value: Int)<!> {
	someOtherProperty = value.toChar()
}
