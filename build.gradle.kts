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
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	// Fabric
	minecraft(libs.minecraft)
	implementation(libs.fabric.loader)

	// Fabric API
	implementation(libs.fabric.api)

	// Mixin extras
	annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.3")

	// Meteor Client
	implementation(libs.baritone)
	implementation(libs.meteor.client)

	// Xaero's Mods
	compileOnly(libs.xwm) // Xaero's World Map
	compileOnly(libs.xmm) // Xaero's Minimap
	compileOnly(files("libs\\xaerolib-fabric-26.1.2-1.1.13.jar"))

	// Chest Tracker
	implementation(libs.whereisit)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
	}
}

fun toMinecraftCompat(version: String): String {
	val match = Regex("""^(\d{2})\.([1-9]\d*)(?:\.([1-9]\d*))?$""")
		.matchEntire(version)
		?: error("Invalid Minecraft version format: $version. Expected YY.D or YY.D.H")

	val (year, drop, _) = match.destructured
	return "~$year.$drop"
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
        val licenseSuffix = project.base.archivesName.get()
        from("LICENSE") {
            rename { "${it}_${licenseSuffix}" }
        }
    }
}
