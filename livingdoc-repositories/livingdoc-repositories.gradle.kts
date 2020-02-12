plugins {
	`kotlin-project-config`
}
dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation("com.vladsch.flexmark:flexmark:${Versions.flexmark}")
	implementation("com.vladsch.flexmark:flexmark-ext-tables:${Versions.flexmark}")

	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))
	implementation(project(":livingdoc-format-gherkin"))

	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")

	testImplementation("io.cucumber:gherkin:${Versions.gherkin}")
	testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
}
