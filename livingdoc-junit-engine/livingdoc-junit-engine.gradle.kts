plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-engine"))
	implementation(project(":livingdoc-repositories"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))

	implementation(kotlin("reflect", Versions.kotlinVersion))

	api("org.junit.platform:junit-platform-engine:${Versions.junitPlatform}")
}
