plugins {
	alias(libs.plugins.fabric.loom)
	id("maven-publish")
}

val archivesBaseName = providers.gradleProperty("archives_base_name").get()
val mavenGroup = providers.gradleProperty("maven_group").get()

base {
    archivesName = archivesBaseName
    version = libs.versions.mod.version.get()
    group = mavenGroup
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
	implementation(libs.fabric.loader)

	// Fabric API
	implementation(libs.fabric.api)

	// Meteor Client
	implementation(libs.meteor.client)
	implementation(libs.baritone)

	// Xaero's Mods
	compileOnly(libs.xlib) // XaeroLib
	compileOnly(libs.xwm) // Xaero's World Map
	compileOnly(libs.xmm) // Xaero's Minimap

	// Chest Tracker
	implementation(libs.whereisit)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
	}
}

fun toMinecraftCompat(version: String): String {
	val stable = Regex("""^(\d{2})\.([1-9]\d*)(?:\.(\d+))?$""")

	stable.matchEntire(version)?.let {
		val (year, drop, _) = it.destructured
		return "~$year.$drop"
	}

	val pre = Regex("""^(\d{2})\.([1-9]\d*)-pre[-.](\d+)$""")
	pre.matchEntire(version)?.let {
		return version.replace("-pre-", "-pre.")
	}

	val rc = Regex("""^(\d{2})\.([1-9]\d*)-rc[-.](\d+)$""")
	rc.matchEntire(version)?.let {
		return version.replace("-rc-", "-rc.")
	}

	return version
}

loom {
	accessWidenerPath = file("src/main/resources/meteorplus.accesswidener")
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "minecraft_version" to libs.versions.minecraft.get(),
			"jdk_version" to libs.versions.jdk.get(),
            "gh_hash" to (System.getenv("GITHUB_SHA") ?: ""),
        )

	    filesMatching("fabric.mod.json") {
		    expand (propertyMap)
        }
	}
    jar {
		inputs.property("archivesName", archivesBaseName)

		from("LICENSE") {
			rename { "${it}_$archivesBaseName" }
        }
    }
}
