plugins {
    `kotlin-project-config`
}

dependencies {
	testRuntime(project(":livingdoc-junit-engine"))
	testRuntime(project(":livingdoc-repository-file"))

	testCompile(project(":livingdoc-api"))
	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
