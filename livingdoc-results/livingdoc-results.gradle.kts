plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-testdata"))
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
