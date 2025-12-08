import java.util.EnumSet

const val PROJ_GROUP = "cb77.lang.plugins.kt.overloadablesetters"
const val PROJ_VERSION = "1.0.0"

const val MY_NEXUS_ID = "cbrandt77"

object MyMetaInfo {
	const val plugin_id = PROJ_GROUP
}

object MyPublishingInfo {
	const val name = "OverloadableSetters"
	
	const val description = "Give properties multiple setters! Define multiple ways to set a field, and invoke them with standard property syntax."
	
	const val vcsurl = "https://github.com/cbrandt77/Kotlin-OverloadableSetters"
	const val url = vcsurl
	
	const val license_name = ""
	const val license_url = ""
	
	val developers = listOf(
			"cbrandt77" to "Caleb Brandt"
	)
}

enum class EMyProjectType {
	ANNOTATIONS,
	GRADLE,
	COMPILER,
	TEST_PROJECT;
	
	infix fun and(otherType: EMyProjectType): EnumSet<EMyProjectType> {
		return enumSetOf(this, otherType)
	}
}

fun <T : Enum<T>> enumSetOf(first: T, vararg rest: T): EnumSet<T> {
	return EnumSet.of(first, *rest)
}

//object MyBuildConfigGlobals {
//	const val PACKAGE_NAME = PROJ_GROUP
//	const val CLASS_NAME = "OverloadableSettersBuildConfig"
//
//	data class ConfigField(val first: String, val second: String, val third: String)
//
//	private val metadata = listOf(
//			ConfigField("String", "KOTLIN_PLUGIN_ID", "\"${MyMetaInfo.plugin_id}\"")
//	)
//
//	private val cliOptions = listOf(
//			ConfigField("String", "OPT_ALLOW_AMBIGUOUS_CASE_SETTERS", "\"allow-ambiguous-case\""),
//			ConfigField("String", "OPT_SETTERPATTERN_CLINAME", "\"setter-pattern\"")
//	)
//
//	fun getFieldsForProjectType(type: EMyProjectType): List<ConfigField> {
//		return mutableListOf<ConfigField>().apply {
//			addAll(metadata)
//
//			if (type == EMyProjectType.GRADLE || type == EMyProjectType.COMPILER) {
//				addAll(cliOptions)
//			}
//
//			if (type == EMyProjectType.GRADLE) {
//				// TODO I think I'm spiraling here
//			}
//		}
//	}
//}