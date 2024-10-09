// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

// Thêm phần phụ thuộc cho Firebase
buildscript {
    dependencies {
        // Thêm Firebase plugin
        classpath("com.google.gms:google-services:4.3.10") // hoặc phiên bản mới nhất
    }
}