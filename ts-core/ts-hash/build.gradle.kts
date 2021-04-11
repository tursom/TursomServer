plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":ts-core"))
    api(project(":ts-core:ts-buffer"))
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            try {
                artifact(tasks["sourcesJar"])
            } catch (e: Exception) {
            }
        }
    }
}
