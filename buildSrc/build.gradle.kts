plugins {
	`kotlin-dsl`
	"org.jetbrains.dokka"
	kotlin("jvm") version "1.3.50"
}

repositories {
	jcenter()
}

dependencies {
	implementation(kotlin("gradle-plugin"))
}
