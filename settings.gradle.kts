// Настройки Gradle для проекта
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
    }
}

// Путь к каталогу версий (Version Catalog)
gradle.startParameter.let {
    // Включаем централизованный каталог зависимостей
}

rootProject.name = "EisenhowerMatrix"
include(":app")
