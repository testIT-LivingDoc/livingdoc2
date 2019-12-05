plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.yaml:snakeyaml:${Versions.snakeyaml}")
	implementation(kotlin("reflect"))

	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
