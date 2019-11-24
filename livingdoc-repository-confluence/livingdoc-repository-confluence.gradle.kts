plugins {
	`kotlin-project-config`
}

repositories {
	maven {
		url = uri("https://packages.atlassian.com/mvn/maven-external")
	}
}

dependencies {
	compile("org.slf4j:slf4j-api:${Versions.slf4j}")
	compile("com.atlassian.confluence:confluence-rest-client:7.0.3")
	compile("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
	runtime("org.glassfish.jaxb:jaxb-runtime:2.3.2")
	compile(project(":livingdoc-repositories"))

	testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
	testImplementation("com.github.tomakehurst:wiremock-jre8:${Versions.wiremock}")
	testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}
