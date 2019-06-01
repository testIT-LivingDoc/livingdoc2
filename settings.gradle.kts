pluginManagement {
	repositories {
		gradlePluginPortal()
		jcenter()
	}
	resolutionStrategy {
		eachPlugin {
			when(requested.id.id) {
				"com.gradle.build-scan" -> useVersion(Versions.buildScanPlugin)
				"com.diffplug.gradle.spotless" -> useVersion(Versions.spotlessPlugin)
				"org.jetbrains.kotlin.jvm" -> useVersion(Versions.kotlinVersion)
				"org.asciidoctor.convert" -> useVersion(Versions.asciidoctorPlugin)
				"io.gitlab.arturbosch.detekt" -> useVersion(Versions.detektVersion)
			}
		}
	}
}

rootProject.name = "livingdoc"

include( "livingdoc-api",
"livingdoc-converters",
"livingdoc-documentation",
"livingdoc-engine",
"livingdoc-junit-engine",
"livingdoc-repositories",
"livingdoc-repository-file",
"livingdoc-sample")

rootProject.children.forEach { project ->
	project.buildFileName = "${project.name}.gradle"
	if (!project.buildFile.isFile) {
		project.buildFileName = "${project.name}.gradle.kts"
	}
	require(project.buildFile.isFile) {
		"${project.buildFile} must exist"
	}
}
