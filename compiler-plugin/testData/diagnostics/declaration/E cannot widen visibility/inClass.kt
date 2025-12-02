// RUN_PIPELINE_TILL: BACKEND


// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;

annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters

class MyClass {
	@HasCustomSetters
	private var someProperty: String = ""
	
	fun <!SETTER_DECL_CANNOT_WIDEN_VISIBILITY!>`set-someProperty`<!>(value: Int) {
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
	x.someProperty = 2
}
