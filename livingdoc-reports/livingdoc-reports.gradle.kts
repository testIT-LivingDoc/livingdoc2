plugins {
	`kotlin-project-config`
}

repositories {
	maven {
		url = uri("https://packages.atlassian.com/mvn/maven-external")
	}
}

dependencies {
	implementation("org.codehaus.jackson:jackson-core-asl:1.9.13")
	implementation("org.codehaus.jackson:jackson-mapper-asl:1.9.13")
	implementation("org.codehaus.jackson:jackson-jaxrs:1.9.13")
	implementation("org.codehaus.jackson:jackson-xc:1.9.13")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
	runtimeOnly("org.glassfish.jaxb:jaxb-runtime:2.3.2")
	implementation("com.atlassian.confluence:confluence-rest-client:7.0.3")
	implementation("org.jsoup:jsoup:${Versions.jsoup}")
	implementation("com.beust:klaxon:${Versions.klaxon}")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-results"))
	implementation(project(":livingdoc-repositories"))
	implementation(project(":livingdoc-testdata"))

	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
