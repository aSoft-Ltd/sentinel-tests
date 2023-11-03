plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform server registration sdk"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.sentinel.registration.service.flix)
                implementation(libs.raven.bus)
                implementation(libs.sanity.local)
                implementation(libs.kommander.coroutines)
            }
        }
    }
}