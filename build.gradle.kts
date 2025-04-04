plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
	alias(libs.plugins.ktor)
	alias(libs.plugins.sqldelight)
}

group = "dev.kamilbak.shurl"
version = "0.0.1"

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
	implementation(libs.logback)
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
