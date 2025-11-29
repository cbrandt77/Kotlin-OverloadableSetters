package cb77.lang.plugins.kt.overloadablesetters

/**
 * Placed on a property to signify that it may have overloaded setters declared for it, either locally or by extension methods.
 *
 * Overloaded setters will be resolved like standard property setters:
 * ```
 * // MyClass.kt
 * class MyClass {
 *     // On an annotated property:
 *     @HasCustomSetters
 *     var foo: () -> String = { "" }
 *
 *     // Both in-class methods...
 *     fun `set-foo`(value: String) {
 *         foo = { value }
 *     }
 * }
 * // ...and extension methods...
 * fun MyClass.`set-foo`(value: Int) {
 *     foo = value.toString() // delegates to the above `set-foo`
 * }
 *
 * // ...can be invoked with standard property syntax:
 * fun main() {
 *     val inst = MyClass()
 *
 *     // vanilla property set
 *     inst.foo = { "a deferred string" }
 *     assert(inst.foo() == "a deferred string")
 *
 *     // invokes in-class method `MyClass#set-foo(String)`
 *     inst.foo = "a string literal"
 *     assert(inst.foo() == "a string literal")
 *
 *     // invokes extension method `MyClass.set-foo(Int)`
 *     inst.foo = 0
 *     assert(inst.foo() == "0")
 * }
 * ```
 *
 * Note that setter methods MUST:
 * 1. Be named `set-<propertyName>`
 * 2. Have a single parameter, with no context or type parameters
 * 3. Have no return type (i.e. return `Unit`)
 * 4. **Accept a _different type_ than the original property's setter** (i.e. must not shadow the vanilla property setter)
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public annotation class HasCustomSetters
