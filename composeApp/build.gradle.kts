import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

val releaseVersion = providers.gradleProperty("releaseVersion")
    .orElse(libs.versions.version.name)
    .map { version ->
        val match = Regex("""([1-9]\d*)\.(\d+)\.(\d+)""").matchEntire(version)
        require(match != null) {
            "Invalid releaseVersion '$version'. Native desktop release versions must use MAJOR.MINOR.PATCH with MAJOR > 0."
        }

        val major = match.groupValues[1].toIntOrNull()
        val minor = match.groupValues[2].toIntOrNull()
        val patch = match.groupValues[3].toIntOrNull()
        require(major != null && major <= 255 && minor != null && minor <= 255 && patch != null && patch <= 65535) {
            "Invalid releaseVersion '$version'. Windows MSI requires MAJOR <= 255, MINOR <= 255, and PATCH <= 65535."
        }
        version
    }

kotlin {
    jvm()
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.subbyte.subspectrum.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = libs.versions.app.name.get()
            packageVersion = releaseVersion.get()
            vendor = libs.versions.app.vendor.get()
            description = libs.versions.app.description.get()

            macOS {
                bundleID = libs.versions.app.pkg.get()
                dockName = libs.versions.app.name.get()
                iconFile.set(project.file("../media/icon.icns"))
            }
            windows {
                iconFile.set(project.file("../media/icon.ico"))
                msiPackageVersion = releaseVersion.get()
                shortcut = true
                dirChooser = true
                menu = true
                menuGroup = libs.versions.app.menugroup.get()
            }
            linux {
                iconFile.set(project.file("../media/icon.png"))
                debMaintainer = libs.versions.app.vendor.get()
                menuGroup = libs.versions.app.menugroup.get()
                shortcut = true
            }
        }
    }
}
