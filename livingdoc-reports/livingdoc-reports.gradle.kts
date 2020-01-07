plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation("com.beust:klaxon:${Versions.klaxon}")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-repositories"))

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
