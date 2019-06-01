plugins {
	`kotlin-project-config`
}

dependencies {
	compile(project(":livingdoc-engine"))

	compile("org.junit.platform:junit-platform-engine:${Versions.junitPlatform}")
}
