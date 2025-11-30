// RUN_PIPELINE_TILL: FRONTEND

// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;
annotation class HasCustomSetters

// MODULE: lib
// FILE: foo.kt
package foo;

import annotations.HasCustomSetters

class MyClass {
	var someProperty: String
	
	fun `set-someProperty`(value: Int) {
		someProperty = value.toString()
	}
}


// MODULE: main(lib)
// FILE: bar.kt
package bar;

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.someProperty = <!ARGUMENT_TYPE_MISMATCH!>2<!>
}
