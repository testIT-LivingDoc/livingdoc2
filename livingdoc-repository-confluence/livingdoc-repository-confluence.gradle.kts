plugins {
	`kotlin-project-config`
}

repositories {
	maven {
		url = uri("https://packages.atlassian.com/mvn/maven-external")
	}
}

dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	implementation("com.atlassian.confluence:confluence-rest-client:7.0.3")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
	runtimeOnly("org.glassfish.jaxb:jaxb-runtime:2.3.2")
	implementation(project(":livingdoc-config"))
	implementation(project(":livingdoc-api"))
	implementation(project(":livingdoc-repositories"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
