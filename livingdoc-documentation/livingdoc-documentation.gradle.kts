plugins {
    id("org.asciidoctor.convert")
    `java-project-config`
}

dependencies {
    compile(project(":livingdoc-api"))
    compile("org.assertj:assertj-core:${Versions.assertJ}")
}

tasks {
    asciidoctor {
        // Documentation: http://asciidoctor.org/docs/asciidoctor-gradle-plugin/
        sources(delegateClosureOf<PatternSet> {
            include("index.adoc")
        })
        options(mapOf("doctype" to "book", "backend" to "html5"))
        attributes(
            mapOf(
                "moduleBase" to "${projectDir}",
                "source-highlighter" to "coderay",
                "toc" to "left",
                "toclevels" to "3",
                "sectlinks" to "true",
                "sectnums" to "true"
            )
        )
    }
}

//tasks.build.dependsOn asciidoctor

