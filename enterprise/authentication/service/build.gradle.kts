import docker.DockateExtension
import docker.DockatePlugin

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

apply<DockatePlugin>()

description = "A kotlin multiplatform server registration sdk"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.sentinel.registration.service.flix)
                implementation(libs.sentinel.enterprise.authentication.service.flix)
                implementation(libs.kommander.coroutines)
            }
        }
    }
}

configure<DockateExtension> {
    addDockerContainerTasksForMongo(
        image = "mongodb/mongodb-community-server:7.0.0-ubuntu2204",
        username = "root",
        password = "pass",
        port = 27017
    )
}