import org.gradle.plugins.signing.signatory.internal.gnupg.GnupgSignatoryProvider

plugins {
	`maven-publish`
	signing
}

publishing {
	// Configure all publications
	publications.withType<MavenPublication> {
		// Provide artifacts information required by Maven Central
		pom {
			name.set(MyPublishingInfo.name)
			description.set(MyPublishingInfo.description)
			url.set(MyPublishingInfo.url)
			
			licenses {
				license {
					name.set(MyPublishingInfo.license_name)
					url.set(MyPublishingInfo.license_url)
				}
			}
			developers {
				MyPublishingInfo.developers.forEach { (_id, _name) ->
					developer {
						id.set(_id)
						name.set(_name)
					}
				}
				
			}
			scm {
				url.set(MyPublishingInfo.url)
			}
		}
	}
}

signing {
	sign(publishing.publications)
}