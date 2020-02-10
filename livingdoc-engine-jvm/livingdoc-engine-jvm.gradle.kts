plugins {
	`kotlin-project-config`
	id("me.champeau.gradle.jmh").version("0.5.0")
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-converters"))
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))

	api(project(":livingdoc-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

jmh {
	benchmarkMode = listOf("AverageTime", "SampleTime")
	duplicateClassesStrategy = DuplicatesStrategy.WARN
	fork = 1
	timeUnit = "ms"
}
