package org.livingdoc.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.RepositoryManager.*
import org.livingdoc.repositories.config.Configuration
import org.livingdoc.repositories.config.YamlUtils


internal class RepositoryManagerTest {

    val repositoryFactory = TestRepositoryFactory::class.java.name
    val notARepositoryFactory = NotARepositoryFactory::class.java.name

    @Test fun `multiple repositories can be defined in configuration`() {

        val configYaml = """

            repositories:
              - name: "repo-1"
                factory: "$repositoryFactory"
                config:
                  foo: "test repository one"
                  bar: 1.11
              - name: "repo-2"
                factory: "$repositoryFactory"
                config:
                  foo: "test repository two"
                  bar: 2.22

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        val repositoryManager = RepositoryManager.from(configuration)
        val repository1 = repositoryManager.getRepository("repo-1") as TestRepository
        val repository2 = repositoryManager.getRepository("repo-2") as TestRepository

        assertThat(repository1.config.foo).isEqualTo("test repository one")
        assertThat(repository1.config.bar).isEqualTo(1.11f)

        assertThat(repository2.config.foo).isEqualTo("test repository two")
        assertThat(repository2.config.bar).isEqualTo(2.22f)
    }

    @Test fun `default property values are used if not specified in configuration`() {

        val configYaml = """

            repositories:
              - name: "repo"
                factory: "$repositoryFactory"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        val repositoryManager = RepositoryManager.from(configuration)
        val repository = repositoryManager.getRepository("repo") as TestRepository

        assertThat(repository.config.foo).isNull()
        assertThat(repository.config.bar).isEqualTo(1.0f)

    }

    @Test fun `single repository can omit its name`() {

        val configYaml = """

            repositories:
              - factory: "$repositoryFactory"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        val repositoryManager = RepositoryManager.from(configuration)
        val repository = repositoryManager.getRepository("default") as TestRepository

        assertThat(repository.config.foo).isNull()
        assertThat(repository.config.bar).isEqualTo(1.0f)

    }

    @Test fun `repositories must have unique names`() {

        val configYaml = """

            repositories:
              - name: "repo"
                factory: "$repositoryFactory"
              - name: "repo"
                factory: "$repositoryFactory"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        assertThrows(RepositoryAlreadyRegisteredException::class.java) {
            RepositoryManager.from(configuration)
        }

    }

    @Test fun `repositories must declare a factory class`() {

        val configYaml = """

            repositories:
              - name: "repo"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        assertThrows(NoRepositoryFactoryException::class.java) {
            RepositoryManager.from(configuration)
        }

    }

    @Test fun `repositories must declare valid factory class`() {

        val configYaml = """

            repositories:
              - name: "repo"
                factory: "$notARepositoryFactory"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        assertThrows(NotARepositoryFactoryException::class.java) {
            RepositoryManager.from(configuration)
        }

    }

    @Test fun `exception is thrown for unknown configuration properties`() {

        val configYaml = """

            repositories:
              - name: "repo"
                factory: "$repositoryFactory"
                config:
                  unknown: "foo bar"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        assertThrows(RuntimeException::class.java) {
            RepositoryManager.from(configuration)
        }

    }

    @Test fun `exception is thrown if repository with given name doesn't exist`() {

        val configYaml = """

            repositories:
              - name: "foo"
                factory: "$repositoryFactory"
              - name: "bar"
                factory: "$repositoryFactory"

        """.trimIndent()

        val configuration = Configuration.loadFromStream(configYaml.byteInputStream())
        val repositoryManager = RepositoryManager.from(configuration)
        val exception = assertThrows(RepositoryNotFoundException::class.java) {
            repositoryManager.getRepository("xur")
        }
        assertThat(exception)
                .hasMessageContaining("Repository 'xur' not found in manager. Known repositories are: [foo, bar]")

    }

    class TestRepository(val config: TestRepositoryConfiguration) : DocumentRepository {
        override fun getDocument(documentIdentifier: String): Document = Document(emptyList(), emptyList())
    }

    data class TestRepositoryConfiguration(
            var foo: String? = null,
            var bar: Float = 1.0f
    )

    class TestRepositoryFactory : DocumentRepositoryFactory<TestRepository> {

        override fun createRepository(name: String, configData: Map<String, Any>): TestRepository {
            val config = YamlUtils.toObject(configData, TestRepositoryConfiguration::class)
            return TestRepository(config)
        }

    }

    class NotARepositoryFactory

}
