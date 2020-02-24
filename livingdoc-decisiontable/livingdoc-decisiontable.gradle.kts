plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(kotlin("reflect"))
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-converters"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))

	api(project(":livingdoc-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
	kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
}
