import com.vanniktech.maven.publish.AndroidMultiVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.nexus.plugin)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")
mavenPublishing {
    val artifactId = "content-reveal"

    configure(
        AndroidMultiVariantLibrary(
            sourcesJar = true
        )
    )

    publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(
        "io.github.meglali20",
        artifactId,
        rootProject.extra.get("libVersion").toString()
    )

    pom {
        name.set(artifactId)
        description.set("Reveal content with clipping on Android with Jetpack Compose.")
        url.set("https://github.com/Meglali20/Content-Reveal")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                name.set("Oussama Meglali")
                organizationUrl.set("https://github.com/Meglali20")
                organization.set("Oussama Meglali")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Meglali20/Content-Reveal.git")
            developerConnection.set("scm:git:ssh://github.com/Meglali20/Content-Reveal.git")
            url.set("https://github.com/Meglali20/Content-Reveal")
        }

    }
}

android {
    namespace = "com.oussamameg.contentreveal"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}