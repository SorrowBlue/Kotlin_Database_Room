plugins {
	kotlin("jvm")
	maven
}

group = "com.sorrowblue.kdbc"
version = "0.1.0"

repositories {
	mavenCentral()
}

dependencies {
	api(kotlin("stdlib-jdk8"))
	api("com.squareup:kotlinpoet:1.5.0")
	api("com.google.auto.service:auto-service:1.0-rc6")
}

tasks {
	compileKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
	compileTestKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
	"uploadArchives"(Upload::class) {
		repositories {
			withConvention(MavenRepositoryHandlerConvention::class) {
				mavenDeployer {
					withGroovyBuilder {
						val repo = File(rootDir, "docs")
						"repository"("url" to uri(repo.absolutePath))
					}
					pom.project {
						withGroovyBuilder {
							"parent" {
								"groupId"("com.sorrowblue.kdbc")
								"artifactId"("common")
								"version"(version)
							}
						}
					}
				}
			}
		}
	}
}
