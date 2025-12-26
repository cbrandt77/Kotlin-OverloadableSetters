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

This is primarily useful for builders and DSLs:
```kotlin
myBuilder { 
    defaultValue = "a literal string"
    defaultValue = 0
    defaultValue = { someExpensiveComputation() }
}
```

### Notes About Inheritance

Setters for an inherited property may also be resolved on subclasses, 
as long as the property is annotated either on the type or above it on the inheritance hierarchy.

```kotlin
interface Parent {
    @HasCustomSetters
    var parentProperty: String
}

class Child1 : Parent {
    fun `set-parentProperty`(value: Int) { 
        parentProperty = value.toString()
    }
}

class Child2 : Parent {
    fun `set-parentProperty`(value: Int) {
        throw IllegalArgumentException("No Ints Allowed")
    }
}

Child1().parentProperty = 2 // Calls `Child1#set-parentProperty`
Child2().parentProperty = 2 // Calls `Child2#set-parentProperty`


interface UnannotedParent {
    var unannotatedProperty: String
}

class Child : UnannotatedParent { 
    @HasCustomSetters
    override var unannotatedProperty: String = ""
}

fun Child.`set-unannotatedProperty`(value: Int) {
	this.unannotatedProperty = value.toString()
}

Child().unannotatedProperty = 2 // Works
(Child() as Parent).unannotatedProperty = 2 // ERROR: Not annotated
```

This isn't unique to setters, and is a consideration when creating _any_ method.

## Installation

### Gradle
Add the following to your plugins block:
```kotlin
plugins {
  id("cb77.lang.plugins.kt.overloadablesetters")
}
```


### Enable Diagnostics

To get hints directly in IntelliJ IDEA, including linking to custom setters, validating signatures, and more:
- Enable K2 Mode for the Kotlin IntelliJ plugin.
- Open the IntelliJ Registry
  1. In the toolbar, go to "Help > Edit Custom Properties"
  2. Add `idea.is.internal=true`
  3. Restart IDEA
  4. Go to "Tools > Internal Actions > Registry"
- Set the `kotlin.k2.only.bundled.compiler.plugins.enabled` entry to `false`.

Note that diagnostics do not work on IDEA 2025.2 due to a compiler bug fixed in 2025.3. 


