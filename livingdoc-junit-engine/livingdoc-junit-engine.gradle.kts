plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-engine"))
	implementation(project(":livingdoc-reports"))
	implementation(project(":livingdoc-repositories"))
	implementation(kotlin("reflect"))

	api("org.junit.platform:junit-platform-engine:${Versions.junitPlatform}")
}
