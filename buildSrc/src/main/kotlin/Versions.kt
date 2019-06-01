import org.gradle.api.JavaVersion

object Versions {

    //Language
    val jvmTarget = JavaVersion.VERSION_1_8
    val kotlinVersion = "1.3.21"

    //Dependencies
    val slf4j = "1.7.25"
    val snakeyaml= "1.18"
    val jsoup = "1.11.2"
    val flexmark = "0.28.32"

    //Test
    val junitPlatform = "1.3.2"
    val junitJupiter = "5.4.2"
    val mockitoVersion = "2.23.4"
    val mockitoKotlinVersion = "2.1.0"
    val logback = "1.2.3"
    val assertJ = "3.6.2"

    //Plugins
    val buildScanPlugin = "2.2.1"
    val spotlessPlugin = "3.18.0"

    //AsciiDoctor
    val asciidoctorPlugin = "1.5.3"


    //Tools
    val detektVersion = "1.0.0-RC14"
    val ktlint = "0.24.0"




}
