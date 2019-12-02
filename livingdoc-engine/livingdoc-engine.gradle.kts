plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-converters"))
	implementation(project(":livingdoc-repositories"))

	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("com.beust:klaxon:${Versions.klaxon}")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks.compileTestJava {
	options.compilerArgs.add("-parameters")
}
