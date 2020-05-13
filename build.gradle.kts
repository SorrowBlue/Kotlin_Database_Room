plugins {
    kotlin("jvm") version "1.3.72"
    kotlin("kapt")  version "1.3.72"
}

group = "com.sorrowblue.kdbr"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://sorrowblue.com/Kotlin_Database_Room") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":common"))
    kapt(project(":compiler"))
//    implementation("com.sorrowblue.kdbr:common:0.1.1")
//    kapt("com.sorrowblue.kdbr:compiler:0.1.1")
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
