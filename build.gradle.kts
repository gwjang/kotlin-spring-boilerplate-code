import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.run.BootRun

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    kotlin("plugin.allopen") version "2.1.21"
    kotlin("kapt") version "2.1.21"
    kotlin("plugin.jpa") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "gwjang"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_21

allprojects {
    apply {
        plugin("java-library")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("kotlin-kapt")
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

subprojects {

    tasks.withType<Test> {
        useJUnitPlatform()
    }
    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        val developmentOnly by configurations
        val testImplementation by configurations

        developmentOnly("org.springframework.boot:spring-boot-devtools")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:postgresql")
    }

    extra.apply {
        set("projectName", "${extra.get("rootProjectName")}-$project.name")
    }

    configure<SpringBootExtension> {
        mainClass.set("gwjang.api.ApiApplication")
    }

    configurations.create("testArtifacts") {
        extendsFrom(configurations["testImplementation"])
    }

    tasks.register("testJar", Jar::class.java) {
        from(project.the<SourceSetContainer>()["test"].output)
        dependsOn("testClasses")
        archiveClassifier.set("tests")
    }

    artifacts {
        add("testArtifacts", tasks.named<Jar>("testJar"))
    }

    configure<AllOpenExtension> {
        annotation("javax.persistence.Entity")
        annotation("javax.persistence.MappedSuperclass")
        annotation("javax.persistence.Embeddable")
    }
}

project(":api") {
    dependencies {
        val api by configurations
        val testImplementation by configurations

        api(project(":common"))
        testImplementation(project(":common", "testArtifacts"))
    }
    tasks.getByName<BootRun>("bootRun") {
        environment["SPRING_PROFILES_ACTIVE"] = environment["SPRING_PROFILES_ACTIVE"] ?: "local"
    }
    tasks.register("prepareKotlinBuildScriptModel") {}
}

project(":common") {
    dependencies {
        val api by configurations
        val runtimeOnly by configurations

        api("org.springframework.boot:spring-boot-starter-web")
        api("org.springframework.boot:spring-boot-starter-validation")
        api("org.jetbrains.kotlin:kotlin-reflect")
        api("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("org.postgresql:postgresql")
        api("io.github.wimdeblauwe:error-handling-spring-boot-starter:4.5.0")
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
        api("org.hibernate:hibernate-envers:6.6.8.Final")
        api("org.hibernate:hibernate-spatial:6.6.8.Final")
        developmentOnly("org.springframework.boot:spring-boot-docker-compose")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
    tasks.register("prepareKotlinBuildScriptModel") {}
}
