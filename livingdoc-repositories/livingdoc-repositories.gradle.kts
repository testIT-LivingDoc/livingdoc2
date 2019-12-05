plugins {
	`kotlin-project-config`
}
dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation(project(":livingdoc-config"))

	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
