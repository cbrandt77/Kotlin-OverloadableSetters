import java.time.Duration

plugins {
	id("io.github.gradle-nexus.publish-plugin")
}

fun getPropertyOrEnv(prop: String, env: String): String {
	return project.properties[prop] as String
}

//nexusPublishing {
//	repositories {
//		sonatype {
//			stagingProfileId = getPropertyOrEnv(prop = "sonatypeStagingProfileId", env = "SONATYPE_STAGING_PROFILE_ID")
//			username = getPropertyOrEnv(prop = "sonatypeUsername", env = "SONATYPE_USERNAME")
//			password = getPropertyOrEnv(prop = "sonatypePassword", env = "SONATYPE_PASSWORD")
//		}
//	}
//	transitionCheckOptions {
//		maxRetries.set(100)
//		delayBetween.set(Duration.ofSeconds(5))
//	}
//}

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