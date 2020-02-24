plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("io.ktor:ktor-client-apache:1.2.5")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-repositories"))

	testRuntimeOnly(project(":livingdoc-junit-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
	testImplementation(project(":livingdoc-repository-file"))
	testImplementation(project(":livingdoc-testdata"))
}
