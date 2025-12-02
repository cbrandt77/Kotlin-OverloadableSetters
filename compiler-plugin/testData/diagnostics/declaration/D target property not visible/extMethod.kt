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
	private var someProperty: String = ""
}

// FILE: MyExtensions.kt
package extensions

import foo.MyClass

fun MyClass.<!SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE!>`set-someProperty`<!>(value: Int) {
	someProperty = value.toString()
}
