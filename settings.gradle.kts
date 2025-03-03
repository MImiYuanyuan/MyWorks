pluginManagement {
    repositories {
        maven { url =uri("https://artifact.bytedance.com/repository/AwemeOpenSDK") }
        maven { url =uri("https://maven.aliyun.com/repository/public") }
        maven { url =uri("https://maven.aliyun.com/repository/google") }
        maven { url =uri("https://repo.huaweicloud.com/repository/maven") }
        maven { url =uri("https://jitpack.io") }
        maven { url =uri("https://developer.huawei.com/repo/") }
        maven { url =uri("https://repo1.maven.org/maven2/") }
        maven { url =uri("https://mvn.mob.com/android") }
        maven { url =uri("https://mirrors.tencent.com/nexus/repository/maven-public/") }
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
        maven { url =uri("https://artifact.bytedance.com/repository/AwemeOpenSDK") }
        maven { url =uri("https://maven.aliyun.com/repository/public") }
        maven { url =uri("https://maven.aliyun.com/repository/google") }
        maven { url =uri("https://repo.huaweicloud.com/repository/maven") }
        maven { url =uri("https://jitpack.io") }
        maven { url =uri("https://developer.huawei.com/repo/") }
        maven { url =uri("https://repo1.maven.org/maven2/") }
        maven { url =uri("https://mvn.mob.com/android") }
        maven { url =uri("https://mirrors.tencent.com/nexus/repository/maven-public/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
