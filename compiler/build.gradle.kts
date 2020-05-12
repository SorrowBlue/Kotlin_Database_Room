plugins {
	kotlin("jvm")
	kotlin("kapt")
	maven
}

group = "com.sorrowblue.kdbc"
version = "0.0.3"

repositories {
	mavenCentral()
	maven { url = uri("https://raw.githubusercontent.com/SorrowBlue/Kotlin_Database_Room/master/repository") }
}

dependencies {
//	implementation(project(":common"))
	api("com.sorrowblue.kdbc:common:$version")
	api(kotlin("stdlib-jdk8"))
	api("com.squareup:kotlinpoet:1.5.0")
	api("com.google.auto.service:auto-service:1.0-rc6")
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
								"version"(version)
							}
						}
					}
				}
			}
		}
	}
}
