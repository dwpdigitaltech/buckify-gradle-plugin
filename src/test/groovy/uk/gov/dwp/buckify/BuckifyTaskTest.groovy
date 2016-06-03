package uk.gov.dwp.buckify

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class BuckifyTaskTest {
    @Test
    public void pluginShouldAddTaskToProject() {
        Project testProject = ProjectBuilder.builder().withProjectDir(new File("src/test/resources/dummy-java-groovy-project")) build()
        Project childProject = ProjectBuilder.builder().withName("child").withParent(testProject).build()

        childProject.extensions.create("buckify", BuckifyExtension, "console")
        childProject.plugins.apply('java')
        childProject.plugins.apply('groovy')

        childProject.dependencies{
            compile 'commons-lang:commons-lang:2.5'
        }

        childProject.repositories {
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/releases' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/groups/public/' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/snapshots' }
            maven { url 'https://repo.gradle.org/gradle/libs' }
            mavenLocal()
        }

        testProject.extensions.create("buckify", BuckifyExtension, "console")
        testProject.plugins.apply('java')
        testProject.plugins.apply('groovy')
        testProject.repositories {
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/releases' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/groups/public/' }
            maven { url 'http://bld1.infra.uk1.uc:8081/nexus/content/repositories/snapshots' }
            maven { url 'https://repo.gradle.org/gradle/libs' }
            mavenLocal()
        }

        testProject.dependencies {
            compile project(path: ':child')
            compile 'commons-lang:commons-lang:2.6'
            compile 'joda-time:joda-time:2.2'
            compile group: 'org.apache.solr', name: 'solr-core', version: '6.0.1'
            testCompile group: 'junit', name: 'junit', version: '4.11'
        }

        def buckifyTask = testProject.task('buckify', type: BuckifyTask)
        buckifyTask.actions.each { it.execute(buckifyTask) }
    }
}