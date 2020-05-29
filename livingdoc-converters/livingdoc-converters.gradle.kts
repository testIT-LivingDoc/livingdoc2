plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation(kotlin("reflect", Versions.kotlinVersion))
	implementation(project(":livingdoc-api"))
	implementation("com.beust:klaxon:${Versions.klaxon}")

	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
