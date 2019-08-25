import org.gradle.api.JavaVersion

object Versions {

    //Language
    val jvmTarget = JavaVersion.VERSION_1_8
    val kotlinVersion = "1.3.50"
    val kotlinApi = "1.3"

    //Dependencies
    val slf4j = "1.7.25"
    val snakeyaml= "1.18"
    val jsoup = "1.11.2"
    val flexmark = "0.28.32"

    //Test
    val junitPlatform = "1.5.1"
    val junitJupiter = "5.5.1"
    val mockk = "1.9.3"
    val logback = "1.2.3"
    val assertJ = "3.11.1"

    //Plugins
    val buildScanPlugin = "2.2.1"
    val spotlessPlugin = "3.23.0"

    //AsciiDoctor
    val asciidoctorPlugin = "1.5.3"


    //Tools
    val detektVersion = "1.0.1"
    val ktlint = "0.33.0"
}
