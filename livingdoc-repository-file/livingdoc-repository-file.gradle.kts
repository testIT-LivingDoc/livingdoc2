plugins {
    `kotlin-project-config`
}

dependencies {
	compile("org.slf4j:slf4j-api:${Versions.slf4j}")
	compile("org.jsoup:jsoup:${Versions.jsoup}")
	compile("com.vladsch.flexmark:flexmark:${Versions.flexmark}")
	compile("com.vladsch.flexmark:flexmark-ext-tables:${Versions.flexmark}")
	compile(project(":livingdoc-repositories"))

	testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
	testCompile("org.assertj:assertj-core:${Versions.assertJ}")
	testCompile("org.mockito:mockito-core:${Versions.mockitoVersion}")
	testCompile("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}")
}
