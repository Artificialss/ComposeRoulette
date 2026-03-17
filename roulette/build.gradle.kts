plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

group = "com.artificialss"
version = "1.0.2"

android {
    namespace = "com.artificialss.roulette"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    jvm()
    js(IR) { browser() }
    wasmJs { browser() }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("Compose Roulette")
            description.set("A spin-to-win roulette wheel for Compose Multiplatform. Pure Canvas, no images, no third-party libs.")
            url.set("https://github.com/Artificialss/ComposeRoulette")
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("artificialss")
                    name.set("Artificialss")
                    url.set("https://artificialss.com")
                }
            }
            scm {
                url.set("https://github.com/Artificialss/ComposeRoulette")
                connection.set("scm:git:git://github.com/Artificialss/ComposeRoulette.git")
                developerConnection.set("scm:git:ssh://git@github.com/Artificialss/ComposeRoulette.git")
            }
        }
    }
}
