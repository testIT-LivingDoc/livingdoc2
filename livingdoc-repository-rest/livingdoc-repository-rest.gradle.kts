plugins {
	`kotlin-project-config`
}

dependencies {
	compile("org.slf4j:slf4j-api:${Versions.slf4j}")
	compile(project(":livingdoc-repositories"))
	compile(project(":livingdoc-repository-file"))
	compile("io.ktor:ktor-client-apache:1.2.5")


	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
