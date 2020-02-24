plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
