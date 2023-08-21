plugins {
    `java-library`
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}


tasks.withType<Javadoc> {
    options {
        encoding = "UTF-8"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "flink-connector-mqtt"
            from(components["java"])
            pom {
                name.set("flink-connector-mqtt")
                description.set("Implementation of MQTT connector based on the latest FLIP-27 architecture in Flink")
                url.set("https://github.com/davidfantasy/flink-connector-mqtt")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        name.set("davidfantasy")
                        email.set("davidfantasy8@163.com")
                    }
                }
                scm {
                    connection.set("scm:git:git:https://github.com/davidfantasy/flink-connector-mqtt.git")
                    developerConnection.set("scm:git:git:https://github.com/davidfantasy/flink-connector-mqtt.git")
                    url.set("https://github.com/davidfantasy/flink-connector-mqtt")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("OSSRH_USERNAME") as String?
                password = findProperty("OSSRH_PASSWORD") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications.getByName<MavenPublication>("mavenJava"))
}

group = "com.github.davidfantasy.flink.connector.mqtt"
version = "1.0.0"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    mavenCentral()
}

tasks.compileJava {
    options.encoding = "UTF-8" // 指定编码字符集为UTF-8
}

val flinkVersion = "1.17.1" // flink版本

dependencies {
    compileOnly("org.apache.flink:flink-java:$flinkVersion")
    compileOnly("org.apache.flink:flink-connector-base:$flinkVersion")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("com.hivemq:hivemq-mqtt-client:1.3.1")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    testImplementation("org.apache.flink:flink-clients:$flinkVersion")
    testImplementation("org.apache.flink:flink-java:$flinkVersion")
    testImplementation("org.apache.flink:flink-connector-base:$flinkVersion")
}

tasks.test {
    useJUnitPlatform()
}