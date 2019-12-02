plugins {
	`kotlin-project-config`
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation("com.vladsch.flexmark:flexmark:${Versions.flexmark}")
	implementation("com.vladsch.flexmark:flexmark-ext-tables:${Versions.flexmark}")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-repositories"))

	testRuntimeOnly(project(":livingdoc-junit-engine"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
	testImplementation(project(":livingdoc-api"))
}
