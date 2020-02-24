plugins {
	`kotlin-project-config`
	id("me.champeau.gradle.jmh").version("0.5.0")
}

dependencies {
	implementation(kotlin("reflect"))
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation(project(":livingdoc-api"))

	implementation(project(":livingdoc-converters"))
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-repositories"))
	implementation(project(":livingdoc-engine"))
	implementation(project(":livingdoc-reports"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

jmh {
	benchmarkMode = listOf("AverageTime", "SampleTime")
	duplicateClassesStrategy = DuplicatesStrategy.WARN
	fork = 1
	timeUnit = "ms"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
	kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
}
