plugins {
	`kotlin-project-config`
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-repositories"))

	implementation("org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit}")

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
