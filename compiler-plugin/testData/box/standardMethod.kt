annotation class HasCustomSetters

class MyClass {
	@HasCustomSetters
	var bar: String = "MADBAGIC"
	
	fun setBar(value: Int) {
		bar = value.toString()
	}
}

fun box(): String {
	val inst = MyClass()
	inst.bar = 0
	val result = inst.bar
	return if (result == "0") "OK" else "Fail: $result"
}

