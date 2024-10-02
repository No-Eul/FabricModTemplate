plugins {
	id("java")
	id("fabric-loom") version "1.8.6"
}

apply(from = "https://raw.githubusercontent.com/No-Eul/Buildscripts/fabric-mod/1.0/build.gradle.kts")

repositories {
	mavenCentral()
	maven("https://maven.terraformersmc.com/releases")
	maven("https://api.modrinth.com/maven")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

	modRuntimeOnly("com.terraformersmc:modmenu:${property("mod_menu_version")}")
	modRuntimeOnly("maven.modrinth:mixintrace:1.1.1+1.17")
	modRuntimeOnly("maven.modrinth:language-reload:1.6.1+1.21") // https://modrinth.com/mod/language-reload/versions
}

tasks {
	processResources {
		inputs.properties("fabric_api_version" to project.property("fabric_api_version"))
	}
}
