import kotlin.jvm.JvmName

class MyClass {
	var bar: String = "MADBAGIC"
	
	@JvmName($$$"$$OverloadableSetters$set-bar")
	fun `set-bar`(value: String) {
		bar = value
	}
}

fun box(): String {
	val inst = MyClass()
	inst.`set-bar`("a string")
	val result = inst.bar
	return if (result == "a string") "OK" else "Fail: $result"
}
