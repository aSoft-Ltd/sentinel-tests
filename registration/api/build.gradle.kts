plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform server registration sdk"

kotlin {
    jvm { library() }
    if (Targeting.JS) js(IR) { library() }
//    if (Targeting.WASM) wasm { library() }
    val osxTargets = if (Targeting.OSX) osxTargets() else listOf()
    val ndkTargets = if (Targeting.NDK) ndkTargets() else listOf()
    val linuxTargets = if (Targeting.LINUX) linuxTargets() else listOf()
    val mingwTargets = if (Targeting.MINGW) mingwTargets() else listOf()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.sentinel.registration.api.flix)
                implementation(kotlinx.serialization.json)
                implementation(libs.lexi.console)
                implementation(libs.raven.flix.receiver)
                implementation(libs.kommander.coroutines)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(ktor.client.cio)
            }
        }
    }
}