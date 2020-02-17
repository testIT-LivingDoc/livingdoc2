plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-extensions-api"))

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
