pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            url = uri("D:\\AndroidProject\\expressive-settings\\build\\repo")
        }
    }
    versionCatalogs {
        create("common") {
            from(files("gradle/common.versions.toml"))
        }
    }
}

rootProject.name = "SsPai You"
include(":app")