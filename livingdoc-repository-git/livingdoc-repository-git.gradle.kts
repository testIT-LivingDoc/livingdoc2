plugins {
	`kotlin-project-config`
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-extensions-api"))
	implementation(project(":livingdoc-repositories"))

	implementation(group = "org.eclipse.jgit", name = "org.eclipse.jgit", version = Versions.jgit)

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
