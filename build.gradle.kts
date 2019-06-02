plugins {
	id("io.gitlab.arturbosch.detekt")
	id("com.gradle.build-scan")
	id("com.diffplug.gradle.spotless")
}

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
		project(":livingdoc-converters"),
		project(":livingdoc-engine"),
		project(":livingdoc-junit-engine"),
		project(":livingdoc-repositories"),
		project(":livingdoc-repository-file")
	)
)

val livingdocSampleProjects by extra(
	listOf(
		project(":livingdoc-sample")
	)
)

val kotlinProjects by extra(livingdocKotlinProjects + livingdocSampleProjects)

allprojects {
	apply(plugin = "idea")
	apply(plugin = "com.diffplug.gradle.spotless")

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

	if(project in livingdocKotlinProjects) {
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
					destination = file("${rootProject.projectDir}/build/reports/detekt/${project.name}.html")
				}
			}
		}

		tasks.withType<Delete> {
			delete("${rootProject.projectDir}/build")
		}
	}
}

rootProject.apply {
	description = "LivingDoc"

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
}
