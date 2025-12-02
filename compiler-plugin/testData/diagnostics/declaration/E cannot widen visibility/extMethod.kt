// RUN_PIPELINE_TILL: FRONTEND

// MODULE: lib
// FILE: HasCustomSetters.kt
package annotations;
annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters


class MyOuterClass {
	class MyClass {
		@HasCustomSetters
		private var someProperty: String = ""
	}
	
	fun MyClass.<!SETTER_DECL_CANNOT_WIDEN_VISIBILITY!>`set-someProperty`<!>(value: Int) {
		someProperty = value.toString()
	}
}

// MODULE: main(lib)
// FILE: bar.kt
package bar;

import foo.MyOuterClass.MyClass
import foo.MyOuterClass.`set-someProperty`

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.someProperty = 2
}
