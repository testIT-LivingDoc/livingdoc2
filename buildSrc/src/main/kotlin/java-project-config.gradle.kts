plugins {
	`java-library`
	`maven-publish`
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
}
