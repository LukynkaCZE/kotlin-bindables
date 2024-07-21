plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.23"
}

group = "cz.lukynka"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    compileJava {
        targetCompatibility = "21"
    }
    compileTestJava {
        targetCompatibility = "21"
    }
}

kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        maven {
            url = uri("https://mvn.devos.one/releases")
            credentials {
                username = System.getenv()["MAVEN_USER"]
                password = System.getenv()["MAVEN_PASS"]
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            groupId = "cz.lukynka"
            artifactId = "kotlin-bindables"
            version = version
            from(components["java"])
        }
    }
}