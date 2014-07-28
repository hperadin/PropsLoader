resolvers := Seq(
  "Instantor Nexus" at "http://www.instantor.com/nexus/content/groups/public/"
, Resolver.url("Instantor Nexus (Ivy)", new URL("http://www.instantor.com/nexus/content/groups/public/"))(Resolver.ivyStylePatterns)
)

externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)

addSbtPlugin("com.instantor" % "sbt-instantor-plugin" % "0.3.20")
