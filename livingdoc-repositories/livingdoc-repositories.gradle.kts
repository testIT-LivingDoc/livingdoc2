plugins {
	`kotlin-project-config`
}
dependencies {
	implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
	api("org.yaml:snakeyaml:${Versions.snakeyaml}")
	api("org.jsoup:jsoup:${Versions.jsoup}")

	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
	testCompile("org.mockito:mockito-core:${Versions.mockitoVersion}")
	testCompile("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}")
}
