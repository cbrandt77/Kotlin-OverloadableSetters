import kotlin.jvm.JvmName

class MyClass {
	var bar: String = "MADBAGIC"
	
	@JvmName($$$"$$OverloadableSetters$setBar")
	fun setBar(value: String) {
		bar = value
	}
}

fun box(): String {
	val inst = MyClass()
	inst.setBar("a string")
	val result = inst.bar
	return if (result == "a string") "OK" else "Fail: $result"
}
