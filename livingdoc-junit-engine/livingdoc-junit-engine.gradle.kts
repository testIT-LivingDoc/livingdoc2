plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-engine"))
	implementation(project(":livingdoc-repositories"))

	api("org.junit.platform:junit-platform-engine:${Versions.junitPlatform}")
}
