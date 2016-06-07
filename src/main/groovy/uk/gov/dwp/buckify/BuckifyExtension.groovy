package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.GroovyPlugin
import uk.gov.dwp.buckify.dependencies.ArtifactDependency
import uk.gov.dwp.buckify.dependencies.ProjectDependency

class BuckifyExtension {

    static final String NAME = "buckify"

    DependencyResolution projectDependencies = new DependencyResolution(
            { ProjectDependency dep -> dep.ruleName + ":" + javaLibrary.defaultRuleName },
            { ResolvedArtifact artifact -> artifact.name }
    )
    DependencyResolution externalDependencies = new DependencyResolution(
            { ArtifactDependency dep -> "//lib:" + dep.ruleName },
            { ResolvedArtifact artifact -> artifact.artifact.toString() }
    )
    Closure binaryJarResolution = { String dep -> ":" + dep + "-mvn" }
    GroovyLibrary groovyLibrary = new GroovyLibrary()
    JavaTestLibrary javaTestLibrary = new JavaTestLibrary()
    JavaLibrary javaLibrary = new JavaLibrary()
    boolean autoDeps = true

    void externalDependencies(Closure pathResolution, Closure nameResolution) {
        this.externalDependencies = new DependencyResolution(pathResolution, nameResolution)
    }

    void projectDependencies(Closure pathResolution, Closure nameResolution) {
        this.projectDependencies = new DependencyResolution(pathResolution, nameResolution)
    }

    void groovyLibrary(Closure predicate) {
        this.groovyLibrary = new GroovyLibrary(predicate: predicate)
    }

    static BuckifyExtension from(Project project) {
        project.extensions.findByType(BuckifyExtension)
    }

    class GroovyLibrary {
        Closure predicate = { Project project ->
            def plugin = project.plugins.hasPlugin(GroovyPlugin)
            def groovySrcDir = project.file("src/main/groovy")
            println "has plugin: " + plugin
            println "groovy src dir: " + groovySrcDir.absoluteFile
            println "groovy src dir exists: " + groovySrcDir.exists()
            plugin && groovySrcDir.exists()
        }
        String defaultRuleName = "main-groovy"
    }

    class JavaLibrary {
        String defaultRuleName = "main"
    }

    class JavaTestLibrary {
        String defaultRuleName = "test"
    }

    class DependencyResolution {
        Closure pathResolution
        Closure nameResolution

        DependencyResolution(Closure pathResolution, Closure nameResolution) {
            this.pathResolution = pathResolution
            this.nameResolution = nameResolution
        }
    }
}
