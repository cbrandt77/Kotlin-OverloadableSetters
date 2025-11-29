class MyClass {
	var bar: String = "MADBAGIC"
}

fun box(): String {
	val inst = MyClass()
	inst.bar = "a string"
	val result = inst.bar
	return if (result == "a string") "OK" else "Fail: $result"
}