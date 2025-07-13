@file:Suppress("UnstableApiUsage")

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import kotlin.io.path.Path
import kotlin.io.path.exists

plugins {
	// Fabric Loom - https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/maven-metadata.xml
	id("java")
	id("fabric-loom") version "1.11.4"
	id("com.dorongold.task-tree") version "4.0.0"
}

repositories {
	mavenCentral()
	maven("https://api.modrinth.com/maven")
	maven("https://maven.terraformersmc.com/releases")
//	maven("https://maven.parchmentmc.org")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	/*mappings(loom.layered() {
		officialMojangMappings()
		parchment("org.parchmentmc.data:${property("parchment_version")}@zip")
	})*/
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
	modImplementation("com.terraformersmc:modmenu:${property("mod_menu_version")}")

	// Language Reloaded - https://modrinth.com/mod/language-reload/versions
	modRuntimeOnly("maven.modrinth:mixintrace:1.1.1+1.17")
	modRuntimeOnly("maven.modrinth:language-reload:1.7.4+1.21.6")
}

// region Toolchain Configurations

val targetJavaVersion = JavaVersion.toVersion(property("targetCompatibility")!!)
java {
	targetCompatibility = targetJavaVersion
	sourceCompatibility = targetJavaVersion
	if (JavaVersion.current() < targetJavaVersion)
		toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion.majorVersion.toInt()))
}

loom {
	sourceSets["main"].resources.files
		.find { file -> file.extension.equals("accesswidener", true) }
		?.let(accessWidenerPath::set)

	mixin.defaultRefmapName.set("${property("mod_id")}.refmap.json")

	runs {
		getByName("client") {
			configName = "Minecraft Client"
			runDir = "run/client"
			ideConfigGenerated(true)
			client()
		}

		getByName("server") {
			configName = "Minecraft Server"
			runDir = "run/server"
			ideConfigGenerated(true)
			server()
		}
	}

	afterEvaluate {
		runs.configureEach {
			vmArgs(
				"-XX:+IgnoreUnrecognizedVMOptions",
				"-XX:+AllowEnhancedClassRedefinition",
				"-XX:HotswapAgent=fatjar",
				"-Dfabric.development=true",
				"-Dmixin.debug.export=true",
				"-Dmixin.debug.verify=true",
//				"-Dmixin.debug.strict=true",
//				"-Dmixin.debug.countInjections=true",
				"-Dmixin.checks.interfaces=true",
				"-Dfabric.fabric.debug.deobfuscateWithClasspath",
				"-Dmixin.hotSwap=true",
			)

			Path(System.getProperty("java.home"), "lib/hotswap/hotswap-agent.jar")
				.takeIf { it.exists() }
				.let { vmArg("-Dfabric.systemLibraries=$it") }

			configurations["compileClasspath"].incoming
				.artifactView {
					componentFilter {
						it is ModuleComponentIdentifier
								&& it.group == "net.fabricmc"
								&& it.module == "sponge-mixin"
					}
				}
				.files
				.firstOrNull()
				.let { vmArg("-javaagent:$it") }
		}
	}
}

// endregion

tasks {
	processResources {
		inputs.properties(
			"mod_id" to project.property("mod_id"),
			"name" to project.property("mod_name"),
			"version" to project.version,
			"java_version" to project.property("targetCompatibility"),
			"minecraft_version" to project.property("minecraft_version"),
			"loader_version" to project.property("loader_version"),
			"fabric_api_version" to project.property("fabric_api_version"),
		)

		filesMatching("fabric.mod.json") {
			expand(inputs.properties)
		}

		doLast {
			fileTree(outputs.files.asPath) {
				include("*.mixins.json")
			}.forEach {
				@Suppress("UNCHECKED_CAST")
				val mixinConfigs = JsonSlurper().parse(it) as MutableMap<String, Any>
				mixinConfigs["refmap"] = loom.mixin.defaultRefmapName.get()
				it.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mixinConfigs)))
			}
		}
	}

	compileJava {
		if (targetJavaVersion >= JavaVersion.VERSION_1_10 || JavaVersion.current().isJava10Compatible)
			options.release.set(targetJavaVersion.majorVersion.toInt())
		options.encoding = "UTF-8"
	}

	jar {
		from("LICENSE.txt")
		exclude(
			"assets/**/*.inkscape.svg",
			"assets/**/*.xcf"
		)

		archiveBaseName.set("${project.property("archivesBaseName")}")
		archiveVersion.set("${project.version}+mc${project.property("minecraft_version")}+fabric")
	}

	remapJar {
		archiveBaseName.set("${project.property("archivesBaseName")}")
		archiveVersion.set("${project.version}+mc${project.property("minecraft_version")}+fabric")
	}
}
