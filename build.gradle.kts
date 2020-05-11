plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("kapt")  version "1.3.72"
}

group = "com.sorrowblue.kdbc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://raw.githubusercontent.com/SorrowBlue/Kotlin_Database_Room/master/repository") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":common"))
//    implementation("com.sorrowblue.kdbc:common:0.0.1")
    kapt(project(":compiler"))
    implementation("org.postgresql:postgresql:42.2.12")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
