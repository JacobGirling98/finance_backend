package config

data class EnvironmentVariables(
    val profile: String
)

val environmentVariables = EnvironmentVariables(
    System.getenv("PROFILE")
)

val properties = readProperties(environmentVariables.profile)
