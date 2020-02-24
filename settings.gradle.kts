pluginManagement {
	repositories {
		gradlePluginPortal()
		jcenter()
	}
	resolutionStrategy {
		eachPlugin {
			when (requested.id.id) {
				"com.gradle.build-scan" -> useVersion(Versions.buildScanPlugin)
				"com.diffplug.gradle.spotless" -> useVersion(Versions.spotlessPlugin)
				"org.jetbrains.kotlin.jvm" -> useVersion(Versions.kotlinVersion)
				"org.asciidoctor.convert" -> useVersion(Versions.asciidoctorPlugin)
				"io.gitlab.arturbosch.detekt" -> useVersion(Versions.detektVersion)
				"org.jetbrains.dokka" -> useVersion(Versions.dokkaPlugin)
			}
		}
	}
}

rootProject.name = "livingdoc"

include(
	"livingdoc-api",
	"livingdoc-config",
	"livingdoc-converters",
	"livingdoc-decisiontable",
	"livingdoc-documentation",
	"livingdoc-engine",
	"livingdoc-engine-jvm",
	"livingdoc-format-gherkin",
	"livingdoc-junit-engine",
	"livingdoc-reports",
	"livingdoc-repositories",
	"livingdoc-repository-file",
	"livingdoc-repository-git",
	"livingdoc-repository-rest",
	"livingdoc-repository-confluence",
	"livingdoc-results",
	"livingdoc-scenario",
	"livingdoc-tests",
	"livingdoc-testdata"
)

rootProject.children.forEach { project ->
	project.buildFileName = "${project.name}.gradle"
	if (!project.buildFile.isFile) {
		project.buildFileName = "${project.name}.gradle.kts"
	}
	require(project.buildFile.isFile) {
		"${project.buildFile} must exist"
	}
}
