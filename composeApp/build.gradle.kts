import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
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
            packageName = libs.versions.app.pkg.get()
            packageVersion = libs.versions.version.name.get()
            vendor = libs.versions.app.vendor.get()
            description = libs.versions.app.description.get()

            macOS {
                dockName = libs.versions.app.name.get()
                iconFile.set(project.file("../media/icon.icns"))
            }
            windows {
                iconFile.set(project.file("../media/icon.ico"))
                msiPackageVersion = libs.versions.version.name.get()
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
