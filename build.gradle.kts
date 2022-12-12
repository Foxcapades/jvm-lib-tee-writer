import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.21"
  id("org.jetbrains.dokka") version "1.7.20"
  `maven-publish`
  signing
}

group = "io.foxcapades.lib"
version = "1.0.0"
description = "IO Writer that tees to any number of underlying writers."

repositories {
  mavenCentral()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.dokkaHtml {
  outputDirectory.set(file("docs/dokka/${project.version}"))
}

val javadocJar = tasks.register<Jar>("javadocJar") {
  dependsOn(tasks.dokkaHtml)
  archiveClassifier.set("javadoc")
  from(file("docs/dokka/${project.version}"))
}

tasks.withType<Jar> {
  enabled = true
}

publishing {
  repositories {
    maven {
      name = "Sonatype"
      url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = project.findProperty("nexus.user") as String? ?: System.getenv("NEXUS_USER")
        password = project.findProperty("nexus.pass") as String? ?: System.getenv("NEXUS_PASS")
      }
    }
  }

  publications {
    withType<MavenPublication> {
      artifact(javadocJar)

      pom {
        name.set("tee-writer")
        description.set(project.description)
        url.set("https://github.com/Foxcapades/jvm-lib-tee-writer")

        licenses {
          license {
            name.set("MIT")
          }
        }

        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("foxcapades.io@gmail.com")
            url.set("https://github.com/foxcapades")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/Foxcapades/jvm-lib-tee-writer.git")
          developerConnection.set("scm:git:ssh://github.com/Foxcapades/jvm-lib-tee-writer.git")
          url.set("https://github.com/Foxcapades/jvm-lib-tee-writer")
        }
      }
    }
  }
}

signing {
  useGpgCmd()

  sign(configurations.archives.get())
  publishing.publications.withType<MavenPublication> { sign(this) }
}