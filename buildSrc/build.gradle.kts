plugins {
	`kotlin-dsl`
	"org.jetbrains.dokka"
	kotlin("jvm") version embeddedKotlinVersion
}

repositories {
	jcenter()
}

dependencies {
	implementation(kotlin("gradle-plugin"))
}
