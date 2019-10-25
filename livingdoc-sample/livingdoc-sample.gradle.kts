plugins {
	`kotlin-project-config`
}

dependencies {
	testRuntime(project(":livingdoc-junit-engine"))
	testRuntime(project(":livingdoc-repository-file"))

	testImplementation(project(":livingdoc-api"))
	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
