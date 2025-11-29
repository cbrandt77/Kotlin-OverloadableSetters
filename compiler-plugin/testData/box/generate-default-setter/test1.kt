annotation class HasCustomSetters

class MyClass {
	@HasCustomSetters
	var bar: String = "MADBAGIC"
}

fun box(): String {
	return "OK"
}