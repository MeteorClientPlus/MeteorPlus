plugins {
	alias(libs.plugins.fabric.loom)
	id("maven-publish")
}

base {
    archivesName = properties["archives_name"] as String
    version = libs.versions.mod.version.get()
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
	}
	// Meteor Client
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
	minecraft(libs.minecraft)
	mappings(loom.officialMojangMappings())
	modImplementation(libs.fabric.loader)

	// Fabric API
	modImplementation(libs.fabric.api)

	// Mixin extras
	annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.3")

	// Meteor Client
	modImplementation(files("libs\\baritone-unoptimized-fabric-1.15.0-2-gf7a53504.jar"))
	modImplementation(libs.meteor.client)
	implementation(libs.starscript)
	implementation(libs.orbit)

	// Xaero's Mods
	modCompileOnly(libs.xwm) // Xaero's World Map
	modCompileOnly(libs.xmm) // Xaero's Minimap
	modCompileOnly(files("libs\\xaerolib-fabric-1.21.11-1.0.38.jar"))

	// Chest Tracker
	modImplementation(libs.whereisit)
}

loom {
	accessWidenerPath = file("src/main/resources/meteorplus.accesswidener")
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "mc_version" to libs.versions.minecraft.get(),
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
