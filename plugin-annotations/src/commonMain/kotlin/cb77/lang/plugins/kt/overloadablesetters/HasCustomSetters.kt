package cb77.lang.plugins.kt.overloadablesetters

/**
 * Placed on a property to signify that it may have overloaded setters declared for it, either locally or by extension methods.
 *
 * Overloaded setters will be resolved like standard property setters:
 * ```
 * // MyClass.kt
 * class MyClass {
 *     @HasCustomSetters
 *     var foo: () -> String = { "" }
 *
 *     // Either in-class functions...
 *     fun `set-foo`(value: String) {
 *         foo = { value }
 *     }
 * }
 * // ...or extension functions...
 * fun MyClass.`set-foo`(value: Int) {
 *     foo = value.toString() // delegates to the above `set-foo`
 * }
 *
 * // ...can be invoked with standard property syntax:
 * fun main() {
 *     val inst = MyClass()
 *     inst.foo = { "a deferred string" } // vanilla property set
 *     assert(inst.foo() == "a deferred string")
 *     inst.foo = "a string literal"      // invokes in-class method `MyClass#set-foo(String)`
 *     assert(inst.foo() == "a string literal")
 *     inst.foo = 0                       // invokes extension method `MyClass.set-foo(Int)`
 *     assert(inst.foo() == "0")
 * }
 * ```
 *
 * Note that setter methods **must**:
 * 1. Be named `set-{property}`;
 * 2. Return `Unit`; and
 * 3. Accept a _different type_ than the original property's setter.
 */
@Target(AnnotationTarget.PROPERTY)
public annotation class HasCustomSetters
