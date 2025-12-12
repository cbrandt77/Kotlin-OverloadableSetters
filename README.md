# Overloadable Setters for Kotlin

Give properties multiple setters!  Define multiple ways to set a field, and invoke them with standard property syntax!

Overloaded setters will be resolved like standard property setters:
```kotlin
// MyClass.kt
class MyClass {
    // On an annotated property:
    @HasCustomSetters
    var foo: () -> String = { "" }

    // Both in-class methods...
    fun `set-foo`(value: String) {
        foo = { value }
    }
}
// ...and extension methods...
fun MyClass.`set-foo`(value: Int) {
    foo = value.toString() // delegates to the above `set-foo`
}

// ...can be invoked with standard property syntax:
fun main() {
    val inst = MyClass()

    // vanilla property set
    inst.foo = { "a deferred string" }
    assert(inst.foo() == "a deferred string")

    // invokes in-class method `MyClass#set-foo(String)`
    inst.foo = "a string literal"
    assert(inst.foo() == "a string literal")

    // invokes extension method `MyClass.set-foo(Int)`
    inst.foo = 0
    assert(inst.foo() == "0")
}
```

This is primarily useful for builders:
```kotlin
myBuilder { 
    defaultValue = "a literal string"
    defaultValue = 0
    defaultValue = { someExpensiveComputation() }
}
```

## Installation

<!-- TODO -->
### Gradle
TODO


### Enable Diagnostics

To get linting hints directly in IntelliJ IDEA:
- Enable K2 Mode for the Kotlin IntelliJ plugin.
- Open the IntelliJ Registry
  1. In the toolbar, go to "Help > Edit Custom Properties"
  2. Add `idea.is.internal=true`
  3. Restart IDEA
  4. Go to "Tools > Internal Actions > Registry"
- Set the `kotlin.k2.only.bundled.compiler.plugins.enabled` entry to `false`.



