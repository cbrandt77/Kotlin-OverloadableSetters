plugins {
	id("io.github.gradle-nexus.publish-plugin")
}

allprojects {
	group = PROJ_GROUP
	version = PROJ_VERSION
}

nexusPublishing {
	repositories {
		// see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
		sonatype {
			nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
			snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
			stagingProfileId.set(MY_NEXUS_ID)
		}
	}
}