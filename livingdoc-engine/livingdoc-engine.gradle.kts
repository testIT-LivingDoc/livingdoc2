plugins {
    `kotlin-project-config`
}

dependencies {
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation(project(":livingdoc-api"))
    implementation(project(":livingdoc-converters"))
    implementation(project(":livingdoc-results"))

    api(project(":livingdoc-config"))
    api(project(":livingdoc-repositories"))

    testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
    testImplementation("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks.compileTestJava {
    options.compilerArgs.add("-parameters")
}
