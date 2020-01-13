plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation("com.beust:klaxon:${Versions.klaxon}")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-repositories"))
	implementation(project(":livingdoc-testdata"))

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
