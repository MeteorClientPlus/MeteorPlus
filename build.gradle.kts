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
	// Modrinth
	maven {
		url = uri("https://api.modrinth.com/maven/")
		content {
			includeGroup("maven.modrinth")
		}
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
	// XaeroLib
	maven {
		name = "Xaero's Maven"
		url = uri("https://chocolateminecraft.com/maven")
	}
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	// Fabric
	minecraft(libs.minecraft)
	mappings(variantOf(libs.yarn) { classifier("v2") })
	modImplementation(libs.fabric.loader)

	// Fabric API
	modImplementation(libs.fabric.api)

	// Mixin extras
	annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.3")

	// Meteor Client
	modImplementation(libs.baritone)
	modImplementation(libs.meteor.client)
	implementation(libs.starscript)
	implementation(libs.orbit)

	// Xaero's Mods
	modCompileOnly(libs.xlib)
	modCompileOnly(libs.xwm) // Xaero's World Map
	modCompileOnly(libs.xmm) // Xaero's Minimap

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
