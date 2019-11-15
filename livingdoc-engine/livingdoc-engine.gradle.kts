plugins {
	`kotlin-project-config`
}

dependencies {
	api(project(":livingdoc-api"))
	api(project(":livingdoc-converters"))
	api(project(":livingdoc-repositories"))


	compile("com.github.tomakehurst:wiremock-jre8:2.25.1")

	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("com.beust:klaxon:${Versions.klaxon}")

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks.compileTestJava {
	options.compilerArgs.add("-parameters")
}
