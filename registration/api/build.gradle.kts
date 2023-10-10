import docker.DockateExtension
import docker.DockatePlugin
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

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
                api(libs.sentinel.registration.api.flix)
                api(libs.kommander.coroutines)
            }
        }
    }
}

configure<DockateExtension> {
    val (run, _, remove) = addDockerContainerForMongo(
        image = "mongodb/mongodb-community-server:7.0.0-ubuntu2204",
        username = "root",
        password = "pass",
        port = 27017
    )
    tasks.withType(KotlinJvmTest::class.java).forEach {
        it.dependsOn(run)
        it.finalizedBy(remove)
    }
}
