import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	id("org.sonarqube") version "4.4.1.3373"
}

tasks.bootJar {
	archiveFileName.set("app.jar")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

	all {
		exclude(group = "commons-logging", module = "commons-logging")
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo1.maven.org/maven2/") }
	maven { url = uri("https://maven.java.net/content/groups/public/") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
	testImplementation("org.springframework.security:spring-security-test:6.4.4")

	// Auth0
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.okta.spring:okta-spring-boot-starter:3.0.7")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// Email
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// SendGrid
	implementation("com.sendgrid:sendgrid-java:5.0.0-rc.1")

	// Azure
	implementation("com.azure:azure-storage-blob:12.25.0")
	implementation("com.azure:azure-ai-documentintelligence:1.0.0-beta.1")

	//	Google NLP
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.google.cloud:google-cloud-language:2.0.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:1.0.0")

	//	Swagger Docs
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// Others
	implementation("org.springframework:spring-context:6.2.1")
	implementation("io.github.cdimascio:dotenv-java:3.0.0")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.projectreactor.netty:reactor-netty")
	implementation("io.netty:netty-resolver-dns:4.1.76.Final")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.hateoas:spring-hateoas")
	implementation("org.springframework.data:spring-data-commons")
	implementation("jakarta.persistence:jakarta.persistence-api:2.2.3")
	implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
	implementation("org.glassfish:jakarta.el:4.0.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sonarqube {
	properties {
		property("sonar.projectKey", "your-project-key")
		property("sonar.organization", "your-organization")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}