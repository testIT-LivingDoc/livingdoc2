plugins {
	`kotlin-project-config`
}

dependencies {
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-engine-jvm"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-testdata"))
	runtime(project(":livingdoc-decisiontable"))
	runtime(project(":livingdoc-scenario"))

	implementation(kotlin("reflect", Versions.kotlinVersion))

	implementation("org.junit.jupiter:junit-jupiter-api:${Versions.junitJupiter}")
	api("org.junit.platform:junit-platform-engine:${Versions.junitPlatform}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
	kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
}
