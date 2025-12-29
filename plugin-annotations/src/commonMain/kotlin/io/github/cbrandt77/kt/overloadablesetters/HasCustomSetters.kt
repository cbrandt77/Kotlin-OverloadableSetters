/*
 * Copyright (C) 2025 Caleb Brandt
 *
 * This file is part of Overloadable Setters.
 *
 * Overloadable Setters is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overloadable Setters is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.cbrandt77.kt.overloadablesetters

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
