plugins {
    `kotlin-project-config`
}
dependencies {
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jsoup:jsoup:${Versions.jsoup}")
    implementation("com.vladsch.flexmark:flexmark:${Versions.flexmark}")
    implementation("com.vladsch.flexmark:flexmark-ext-tables:${Versions.flexmark}")
    implementation("io.cucumber:gherkin:${Versions.gherkin}")
    implementation("com.beust:klaxon:${Versions.klaxon}")

    implementation(project(":livingdoc-config"))
    implementation(project(":livingdoc-extensions-api"))
    implementation(project(":livingdoc-results"))
    implementation(project(":livingdoc-testdata"))

    testCompile("ch.qos.logback:logback-classic:${Versions.logback}")
    testCompile("org.assertj:assertj-core:${Versions.assertJ}")
}
