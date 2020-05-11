plugins {
	kotlin("jvm")
	kotlin("kapt")
	maven
}

group = "com.sorrowblue.kdbc"
version = "1.0.0"

repositories {
	mavenCentral()
	maven { url = uri("http://sorrowblue.github.io/Kotlin Database Room/repository") }
}

dependencies {
	implementation(project(":common"))
	implementation(kotlin("stdlib-jdk8"))
	implementation("com.sorrowblue.")
	implementation("com.squareup:kotlinpoet:1.5.0")
	implementation("com.google.auto.service:auto-service:1.0-rc6")
	kapt("com.google.auto.service:auto-service:1.0-rc6")
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
								"artifactId"("compiler")
								"version"("0.0.1")
							}
						}
					}
				}
			}
		}
	}
}
