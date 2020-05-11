plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("kapt")  version "1.3.72"
}

group = "com.sorrowblue.kdbc"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":common"))
    kapt(project(":compiler"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
