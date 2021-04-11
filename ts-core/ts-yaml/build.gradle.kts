plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":ts-core"))
    // 解析YAML
    api(group = "org.yaml", name = "snakeyaml", version = "1.28")
}

@kotlin.Suppress("UNCHECKED_CAST")
(rootProject.ext["excludeTest"] as (Project, TaskContainer) -> Unit)(project, tasks)

tasks.register("install") {
    finalizedBy(tasks["publishToMavenLocal"])
}

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
