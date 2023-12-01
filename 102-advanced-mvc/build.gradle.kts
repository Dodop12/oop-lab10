plugins {
    java
    application
    id("org.danilopianini.gradle-java-qa") version "1.25.0"
}

tasks.javadoc {
    isFailOnError = false
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    compileOnly("com.github.spotbugs:spotbugs-annotations:4.7.3")
}

val mainClass: String by project

application {
    mainClass.set("it.unibo.mvc.DrawNumberApp")
}

val test by tasks.getting(Test::class) {
    // Use junit platform for unit tests
    useJUnitPlatform()
    testLogging {
        events(*(org.gradle.api.tasks.testing.logging.TestLogEvent.values())) // events("passed", "skipped", "failed")
    }
    testLogging.showStandardStreams = true    
}
