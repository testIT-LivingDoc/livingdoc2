plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.yaml:snakeyaml:${Versions.snakeyaml}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
