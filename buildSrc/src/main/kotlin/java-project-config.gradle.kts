plugins {
	`java-library`
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

artifacts {
	archives(javadocJar)
	archives(sourcesJar)
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}
