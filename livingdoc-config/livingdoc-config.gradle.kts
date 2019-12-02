plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.yaml:snakeyaml:${Versions.snakeyaml}")
	compileOnly("org.junit.platform:junit-platform-commons:${Versions.junitPlatform}")
}
