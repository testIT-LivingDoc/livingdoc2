plugins {
	`kotlin-project-config`
}

dependencies {
	testRuntime(project(":livingdoc-junit-engine"))
	testRuntime(project(":livingdoc-repository-file"))
	testRuntime(project(":livingdoc-format-gherkin"))

	testImplementation(project(":livingdoc-api"))
	testImplementation(project(":livingdoc-converters"))
	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
