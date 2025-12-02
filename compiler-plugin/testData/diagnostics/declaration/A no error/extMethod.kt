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
	var someProperty: String = ""
}

// FILE: MyExtensions.kt
package ext

import foo.MyClass

fun MyClass.`set-someProperty`(value: Int) {
	someProperty = value.toString()
}

// MODULE: main(lib)
// FILE: bar.kt
package bar;

import foo.MyClass
import ext.`set-someProperty`

fun test() {
	val x = MyClass()
	x.someProperty = "a string"
	x.someProperty = 2
}
