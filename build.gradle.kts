plugins {
	id("io.gitlab.arturbosch.detekt")
	`build-scan`
	id("com.diffplug.gradle.spotless")
	id("org.jetbrains.dokka")
	kotlin("jvm")
}

rootProject.description = "LivingDoc"

buildScan {
	termsOfServiceUrl = "https://gradle.com/terms-of-service"
	termsOfServiceAgree = "yes"
}

val livingdocJavaProjects by extra(
	listOf(
		project(":livingdoc-api"),
		project(":livingdoc-documentation")
	)
)

val livingdocKotlinProjects by extra(
	listOf(
		project(":livingdoc-config"),
		project(":livingdoc-converters"),
		project(":livingdoc-engine"),
		project(":livingdoc-extensions-api"),
		project(":livingdoc-format-gherkin"),
		project(":livingdoc-junit-engine"),
		project(":livingdoc-reports"),
		project(":livingdoc-repositories"),
		project(":livingdoc-repository-file"),
		project(":livingdoc-repository-git"),
		project(":livingdoc-repository-rest"),
		project(":livingdoc-repository-confluence"),
		project(":livingdoc-results"),
		project(":livingdoc-testdata")
	)
)

val livingdocTestProjects by extra(
	listOf(
		project(":livingdoc-tests")
	)
)

val kotlinProjects by extra(livingdocKotlinProjects + livingdocTestProjects)

allprojects {
	apply(plugin = "idea")
	apply(plugin = "com.diffplug.gradle.spotless")
	apply(plugin = "jacoco")

	group = "org.livingdoc"
	version = "2.0-SNAPSHOT"

	repositories {
		mavenCentral()
		jcenter()
	}
}

subprojects {
	pluginManager.withPlugin("java") {
		spotless {
			java {
				removeUnusedImports()
				trimTrailingWhitespace()
				endWithNewline()
			}
		}
	}

	pluginManager.withPlugin("kotlin") {
		spotless {
			kotlin {
				ktlint(Versions.ktlint)
				trimTrailingWhitespace()
				endWithNewline()
			}
		}
	}

	if (project in livingdocKotlinProjects) {
		apply(plugin = "io.gitlab.arturbosch.detekt")

		detekt {
			toolVersion = Versions.detektVersion

			val configFile = file("${projectDir}/detekt-config.yml")
			val rootConfigFile = file("${rootProject.projectDir}/detekt-config.yml")

			val configList = mutableListOf(rootConfigFile)
			if (configFile.exists()) {
				configList.add(configFile)
			}

			buildUponDefaultConfig = true
			config = files(configList)
			parallel = true
			reports {
				xml.enabled = false
				html {
					enabled = true
					destination = file("${rootProject.buildDir}/reports/detekt/${project.name}.html")
				}
			}
		}

		tasks.withType<Delete> {
			delete(rootProject.buildDir)
		}
	}
}

spotless {
	format("misc") {
		target("**/*.gradle", "**/*.gradle.kts", "**/*.gitignore")
		targetExclude("**/build/**")
		indentWithTabs()
		trimTrailingWhitespace()
		endWithNewline()
	}
	format("documentation") {
		target("**/*.adoc", "**/*.md")
		trimTrailingWhitespace()
		endWithNewline()
	}
}

tasks.create<org.jetbrains.dokka.gradle.DokkaTask>("aggregatedDokka") {

	outputFormat = "javadoc"
	outputDirectory = "$buildDir/dokka"

	subProjects = subprojects.map { it.path.substring(1) }
}

tasks.create<JacocoReport>("codeCoverageReport") {
	executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

	subprojects.onEach {
		it.pluginManager.withPlugin("java") {
			sourceSets(it.the<SourceSetContainer>()["main"])
		}

		it.pluginManager.withPlugin("kotlin") {
			sourceSets(it.the<SourceSetContainer>()["main"])
		}
	}

	reports {
		csv.isEnabled = true
		xml.isEnabled = false
		html.isEnabled = true
		csv.destination = file("${buildDir}/reports/jacoco/jacoco.csv")
		html.destination = file("${buildDir}/reports/jacoco")
	}

	dependsOn(project.getTasksByName("test", true))
}
