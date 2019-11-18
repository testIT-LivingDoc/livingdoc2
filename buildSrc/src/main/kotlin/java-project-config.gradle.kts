plugins {
	`java-library`
	`maven-publish`
	"org.jetbrains.dokka"
	kotlin("jvm")
}

val sourcesJar by tasks.creating(Jar::class) {
	dependsOn(tasks.classes)
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val javadocJar by tasks.creating(Jar::class) {
	archiveClassifier.set("javadoc")
	from(tasks.javadoc)
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifact(sourcesJar)
			artifact(javadocJar)
			from(components["java"])
		}
	}
	repositories {
		maven {
			val projectId = System.getenv("CI_PROJECT_ID")
			val snapshotsRepoUrl =
				"https://gilbert.informatik.uni-stuttgart.de/api/v4/projects/$projectId/packages/maven"
			// only publish snapshots
			url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else "")
			credentials(HttpHeaderCredentials::class.java) {
				name = "Job-Token"
				value = System.getenv("CI_JOB_TOKEN")
			}
			authentication {
				this.create("header", HttpHeaderAuthentication::class.java)
			}
		}
	}
}
