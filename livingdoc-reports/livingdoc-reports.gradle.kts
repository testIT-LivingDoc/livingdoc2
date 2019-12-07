plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation(project(":livingdoc-engine"))
	implementation("com.beust:klaxon:${Versions.klaxon}")

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
