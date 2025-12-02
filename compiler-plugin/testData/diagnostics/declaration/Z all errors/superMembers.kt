// RUN_PIPELINE_TILL: FRONTEND

// MODULE: main
// FILE: HasCustomSetters.kt
package annotations;

annotation class HasCustomSetters

// FILE: MyClass.kt
package foo;

import annotations.HasCustomSetters
/*
SETTER_DECL_CANNOT_WIDEN_VISIBILITY
SETTER_DECL_MUST_HAVE_SINGLE_PARAM
SETTER_DECL_MUST_NOT_HAVE_CONTEXT_PARAMS
SETTER_DECL_MUST_NOT_HAVE_TYPE_PARAMS
SETTER_DECL_MUST_RETURN_UNIT
SETTER_DECL_PARAMETER_SHADOWS_PROPERTY_TYPE
SETTER_DECL_TARGET_PROPERTY_NOT_FOUND
SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE
SETTER_DECL_TARGET_PROPERTY_UNSUPPORTED
*/

sealed class MySuperClass {
	@HasCustomSetters
	public var publicSuperMember: String = ""
	
	
	@HasCustomSetters
	protected var protectedSuperMember: String = ""
	
	
	@HasCustomSetters
	private var privateSuperMember: String = ""
}

class MyClass : MySuperClass() {
	
	//region Super members
	
	
	//region publicSuperMember
	
	fun `set-publicSuperMember`(value: Int) {} // no error
	
	protected fun `set-publicSuperMember`(value: Char) {} // no error
	
	private fun `set-publicSuperMember`(value: Byte) {} // no error
	
	//endregion
	
	
	//region protectedSuperMember
	
	fun <!SETTER_DECL_CANNOT_WIDEN_VISIBILITY!>`set-protectedSuperMember`<!>(value: Int) {}
	
	protected fun `set-protectedSuperMember`(value: Char) {} // No error
	
	private fun `set-protectedSuperMember`(value: Byte) {} // No error
	
	//endregion
	
	
	//region privateSuperMember
	
	fun <!SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE!>`set-privateSuperMember`<!>(value: Int) {}
	
	protected fun <!SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE!>`set-privateSuperMember`<!>(value: Char) {}
	
	private fun <!SETTER_DECL_TARGET_PROPERTY_NOT_VISIBLE!>`set-privateSuperMember`<!>(value: Byte) {} // No error
	
	//endregion
	
	
	//endregion
}
