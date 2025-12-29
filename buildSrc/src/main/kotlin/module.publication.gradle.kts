import org.gradle.plugins.signing.signatory.internal.gnupg.GnupgSignatoryProvider
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

plugins {
	id("com.vanniktech.maven.publish")
	signing
}

mavenPublishing {
	publishToMavenCentral()
	
	signAllPublications()
	
	coordinates(PROJ_GROUP, project.name, PROJ_VERSION)
	
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
			MyPublishingInfo.developers.forEach { (_id, _name, _email, _org, _orgUrl) ->
				developer {
					id.set(_id)
					name.set(_name)
					email.set(_email)
					organization.set(_org)
					organizationUrl.set(_orgUrl)
				}
			}
		}
		
		scm {
			url.set(MyPublishingInfo.vcsurl)
			connection.set(MyPublishingInfo.scmurl)
			developerConnection.set(MyPublishingInfo.scmurl_dev)
		}
	}
}



//tasks.register("publishToMavenCentral") {
//	group = "publishing"
//	description = "Publishes all Maven publications produced by this project to Maven Central."
//	dependsOn("publish")
//	doLast {
//		val username = properties["ossrhUsername"] as String?
//		val password = properties["ossrhPassword"] as String?
//		val namespace = MY_NEXUS_ID
//		val token = Base64.getEncoder().encodeToString("$username:$password".encodeToByteArray())
//		val client = HttpClient.newHttpClient()
//		val request = HttpRequest.newBuilder()
//			.uri(uri("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$namespace"))
//			.header("Authorization", "Bearer $token")
//			.POST(HttpRequest.BodyPublishers.noBody())
//			.build()
//		val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//		if (response.statusCode() < 400) {
//			logger.info(response.body())
//		} else {
//			logger.error(response.body())
//		}
//	}
//}

signing {
//	val signingKey = findProperty("signingKey") as String
//	val signingPassword = findProperty("signingPassword") as String
//	useInMemoryPgpKeys(signingKey, signingPassword)
//	useGpgCmd()
	sign(publishing.publications)
}