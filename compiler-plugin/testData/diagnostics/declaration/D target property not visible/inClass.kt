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