plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform sdk registration"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.sentinel.registration.core)
                api(libs.raven.api)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(db.mongo)
                implementation(libs.raven.smtp)
                implementation(libs.raven.mock)
                implementation(libs.yeti.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kommander.coroutines)
            }
        }
    }
}
