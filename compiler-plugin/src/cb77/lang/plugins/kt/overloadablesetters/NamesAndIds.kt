package cb77.lang.plugins.kt.overloadablesetters

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object NamesAndIds {
	val ANNOT_JVMNAME = FqName("kotlin.jvm.JvmName")
	val ANNOT_HASCUSTOMSETTERS = FqName("cb77.lang.plugins.kt.overloadablesetters.HasCustomSetters")
	val ANNOT_DEFAULTSETTERMETADATA = FqName("cb77.lang.plugins.kt.overloadablesetters.OverloadableSettersDefaultSetterMetadata")
	
	val NAME = Name.identifier("name")
	val TYPE = Name.identifier("type")
	val DEFAULT_SETTER_PARAM_NAME = Name.identifier("value")
}