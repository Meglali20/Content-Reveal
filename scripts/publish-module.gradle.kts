apply(plugin = "com.vanniktech.maven.publish.base")

rootProject.extra.apply {
  val majorVersion = 1
  val minorVersion = 0
  val patchVersion = 0
  val snapshot = System.getenv("SNAPSHOT").toBoolean()
  val libVersion = if (snapshot) {
    "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
  } else {
    "$majorVersion.$minorVersion.$patchVersion"
  }
  set("libVersion", libVersion)
}