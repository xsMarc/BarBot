plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Liegt das Projekt auf einem exFAT-/NTFS-Volume, legt macOS neben jedem
// Verzeichnis eine AppleDouble-Datei ("._name") an. Der Ressourcen-Parser von
// AGP haelt die fuer ein Verzeichnis und bricht ab. Mit
//   ./gradlew -PbarbotBuildDir=/pfad/auf/apfs assembleDebug
// wandert das Build-Verzeichnis deshalb auf eine Platte, die das nicht tut.
// Ohne die Property bleibt alles beim Standard.
val relocatedBuildDir: String? = providers.gradleProperty("barbotBuildDir").orNull
if (relocatedBuildDir != null) {
    allprojects {
        layout.buildDirectory.set(File(relocatedBuildDir, project.name))
    }
}
