plugins {
	`kotlin-project-config`
}

dependencies {
	compile("org.slf4j:slf4j-api:${Versions.slf4j}")
	compile("org.jsoup:jsoup:${Versions.jsoup}")
	compile("com.vladsch.flexmark:flexmark:${Versions.flexmark}")
	compile("com.vladsch.flexmark:flexmark-ext-tables:${Versions.flexmark}")
	compile(project(":livingdoc-repositories"))

	testRuntime(project(":livingdoc-junit-engine"))
	testRuntime(project(":livingdoc-repository-file"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
	testImplementation(project(":livingdoc-api"))
}
