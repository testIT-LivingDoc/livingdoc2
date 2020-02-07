plugins {
	`kotlin-project-config`
}
dependencies {
	implementation("io.cucumber:gherkin:${Versions.gherkin}")
	implementation("com.beust:klaxon:${Versions.klaxon}")

	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))

	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
