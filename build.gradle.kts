plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
	alias(libs.plugins.ktor)
	alias(libs.plugins.sqldelight)
}

group = "dev.kamilbak.shurl"
version = "1.0.0"

application {
	mainClass = "dev.kamilbak.shurl.ApplicationKt"

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	google()
	mavenCentral()
}

dependencies {
	implementation(libs.ktor.server.core)
	implementation(libs.ktor.server.html)
	implementation(libs.ktor.server.netty)
	implementation(libs.kotlinx.html)
	implementation(libs.slf4j.simple)
	implementation(libs.sqldelight)
	implementation(libs.keccak)
}

sqldelight{
	databases{
		create("Database"){
			packageName.set("dev.kamilbak.shurl")
		}
	}
}

tasks.shadowJar{
	minimize()
}
