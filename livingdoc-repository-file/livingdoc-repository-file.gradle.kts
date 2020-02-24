plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-repositories"))

	testRuntimeOnly(project(":livingdoc-junit-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
