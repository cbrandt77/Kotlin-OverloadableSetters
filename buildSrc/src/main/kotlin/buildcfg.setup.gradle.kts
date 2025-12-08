plugins {
	id("com.github.gmazzo.buildconfig")
}

buildConfig {
	packageName(project.group.toString())
	
	className = "OverloadableSettersBuildConfig"
	
	buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
	
	buildConfigField("String", "OPT_USECAMELCASE_CLINAME", "\"camelcase\"")
	buildConfigField("String", "OPT_SETTERPATTERN_CLINAME", "\"setter-pattern\"")
}