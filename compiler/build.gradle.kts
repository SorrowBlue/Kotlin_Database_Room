plugins {
	kotlin("jvm")
	kotlin("kapt")
	maven
}

group = "com.sorrowblue.kdbr"
version = "0.1.2"

repositories {
	mavenCentral()
	maven { url = uri("https://sorrowblue.com/Kotlin_Database_Room") }
}

dependencies {
//	implementation(project(":common"))
	implementation("com.sorrowblue.kdbr:common:$version")
	implementation(kotlin("stdlib-jdk8"))
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
						val repo = File(rootDir, "docs")
						"repository"("url" to uri(repo.absolutePath))
					}
					pom.project {
						withGroovyBuilder {
							"parent" {
								"groupId"("com.sorrowblue.kdbr")
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
