plugins {
	id("fabric-loom") version "1.14-SNAPSHOT"
	id("maven-publish")
}

base {
    archivesName = properties["archives_name"] as String
    version = properties["mod_version"] as String
    group = properties["maven_group"] as String
}

configurations.all {
	// Check for snapshots more frequently than Gradle's default of 1 day. 0 = every build.
	resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

repositories {
	maven {
		url = uri("https://jm.gserv.me/repository/maven-public/")
		content {
			includeGroup("info.journeymap")
		}
	}
	maven {
		url = uri("https://api.modrinth.com/maven/")
		content {
			includeGroup("maven.modrinth")
		}
	}
	maven {
        url = uri("https://www.cursemaven.com") 
    }
	maven { 
        url = uri("https://masa.dy.fi/maven") 
        }
	// YACL
	maven {
		url = uri("https://maven.isxander.dev/releases")
	}
	// YACL Snapshots
	maven {
		name = "Xander Snapshot Maven"
		url = uri("https://maven.isxander.dev/snapshots")
	}
	// Where Is It, JackFredLib
	maven {
		url = uri("https://maven.jackf.red/releases/")
	// Meteor Client
	}
	maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
	maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	// Fabric
	minecraft("com.mojang:minecraft:${properties["minecraft_version"] as String}")
	mappings("net.fabricmc:yarn:${properties["yarn_mappings"] as String}:v2")
	modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"] as String}")

	// Fabric API
	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"] as String}")

	// Mixin extras
	annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.0")

	// Meteor Client
	modImplementation(files("libs\\baritone-unoptimized-fabric-1.15.0-2-gf7a53504.jar"))
	modImplementation("meteordevelopment:meteor-client:${properties["minecraft_version"] as String}-SNAPSHOT")
	implementation("org.meteordev:starscript:0.2.5")
	implementation("meteordevelopment:orbit:0.2.4")

	// Xaero's Mods
	modCompileOnly("maven.modrinth:xaeros-world-map:${properties["xwm_fabric_version"] as String}") // Xaero's World Map
	modCompileOnly("maven.modrinth:xaeros-minimap:${properties["xmm_fabric_version"] as String}") // Xaero's Minimap

	// Chest Tracker
	modImplementation("red.jackf:whereisit:${properties["where_is_it_version"] as String}")
}

loom {
	accessWidenerPath = file("src/main/resources/meteorplus.accesswidener")
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to project.property("minecraft_version"),
            "gh_hash" to (System.getenv("GITHUB_SHA") ?: ""),
        )
        
	    filesMatching("fabric.mod.json") {
		    expand (propertyMap)
        }
	}
    jar {
        val licenseSuffix = project.base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_${licenseSuffix}" }
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }
}