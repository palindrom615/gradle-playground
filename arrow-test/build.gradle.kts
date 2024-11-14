plugins {
    `java`
    kotlin("jvm") version "1.9.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.2.4"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("ch.qos.logback:logback-classic:+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:+")
    testImplementation("org.junit.jupiter:junit-jupiter:+")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
