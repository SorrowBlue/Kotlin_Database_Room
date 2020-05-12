plugins {
	kotlin("jvm")
	maven
}

group = "com.sorrowblue.kdbc"
version = "0.0.3"

repositories {
	mavenCentral()
}

dependencies {
	api(kotlin("stdlib-jdk8"))
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
						val repo = File(rootDir, "repository")
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
