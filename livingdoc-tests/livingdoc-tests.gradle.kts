plugins {
	`kotlin-project-config`
}

dependencies {
	testRuntime(project(":livingdoc-junit-engine"))
	testRuntime(project(":livingdoc-repository-file"))
	testRuntime(project(":livingdoc-repository-rest"))


	testImplementation(project(":livingdoc-api"))
	testImplementation(project(":livingdoc-converters"))
	testImplementation(project(":livingdoc-format-gherkin"))
	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
