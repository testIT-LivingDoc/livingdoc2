plugins {
	`kotlin-project-config`
}

dependencies {
	compile("org.slf4j:slf4j-api:${Versions.slf4j}")
	compile("io.ktor:ktor-client-apache:1.2.5")
	compile(project(":livingdoc-repositories"))

	testRuntime(project(":livingdoc-junit-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
    testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
    testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
	testImplementation(project(":livingdoc-api"))
	testImplementation(project(":livingdoc-repository-file"))
}
