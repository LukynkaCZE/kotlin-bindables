import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.23"
}

group = "cz.lukynka"
version = "1.8"

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

java {
    withSourcesJar()
    withJavadocJar()
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

tasks.withType<PublishToMavenRepository> {
    dependsOn("test")
}

tasks.publish {
    finalizedBy("sendPublishWebhook")
}

task("sendPublishWebhook") {
    group = "publishing"
    description = "Sends a webhook message after publishing to Maven."

    doLast {
        sendWebhookToDiscord(System.getenv("DISCORD_MAYA_WEBHOOK"))
    }
}

fun sendWebhookToDiscord(webhookUrl: String) {
    val httpClient = HttpClient.newHttpClient()

    val requestBody = embed()
    val request = HttpRequest.newBuilder()
        .uri(URI(webhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build()

    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        .thenRun { println("Webhook sent successfully!") }
        .exceptionally { throwable ->
            throwable.printStackTrace()
            null
        }
}


fun embed(): String {
    val target = if(version.toString().endsWith("-SNAPSHOT")) "https://mvn.devos.one/snapshots" else "https://mvn.devos.one/releases"
    val color = if(version.toString().endsWith("-SNAPSHOT")) 16742912 else 65290
    val title = if(version.toString().endsWith("-SNAPSHOT")) "Snapshot Published to Maven" else "Published to Maven"
    return """
        {
          "content": null,
          "embeds": [
            {
              "title": "$title",
              "description": "`cz.lukynka:bindables:$version` was successfully published to maven **$target**!",
              "color": $color
            }
          ],
          "username": "Mavenboo",
          "avatar_url": "https://i2.wp.com/images.genshin-builds.com/zenless/bangboos/BangbooGarageRole02.webp",
          "attachments": []
        }
    """.trimIndent()
}