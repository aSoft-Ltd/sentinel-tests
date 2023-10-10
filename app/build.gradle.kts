import com.android.build.gradle.internal.tasks.factory.dependsOn
import docker.DockateExtension
import docker.DockatePlugin
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    kotlin("jvm")
    id("tz.co.asoft.library")
    application
}

apply<DockatePlugin>()

application {
    mainClass.set("sentinel.MainKt")
}

kotlin {
    target {
        application()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(ktor.server.cio)
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test"))
//                implementation(libs.kommander.coroutines)
            }
        }
    }
}

configure<DockateExtension> {
    val (runMongo, _, removeMongo) = addDockerContainerTasksForMongo(
        image = "mongodb/mongodb-community-server:7.0.0-ubuntu2204",
        username = "root",
        password = "pass",
        port = 27017
    )

    val (createDockerfile, buildAppImage, removeAppImage) = addDockerImageTasksForJvmApp(
        name = "app", port = 8080, directory = layout.buildDirectory.dir("install/$name")
    )

    val (runAppContainer, _, removeAppContainer) = addDockerContainerTasksFor(
        name = "app",
        image = "app",
        args = arrayOf("-p", "8080:8080")
    )

    createDockerfile.configure {
        dependsOn(tasks.named("installDist"))
    }

    runAppContainer.apply {
        dependsOn(runMongo)
        dependsOn(buildAppImage)
    }

    removeAppContainer.configure {
        dependsOn(removeMongo)
        finalizedBy(removeAppImage)
    }

    listOf(
        tasks.withType(KotlinJsTest::class.java).toList(),
        tasks.withType(KotlinJvmTest::class.java).toList()
    ).flatten().forEach {
        it.dependsOn(runAppContainer)
        it.finalizedBy(removeAppContainer)
    }
}
