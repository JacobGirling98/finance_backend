package config

data class EnvironmentVariables(
    val profile: String,
    val githubToken: String
)

val environmentVariables = EnvironmentVariables(
    System.getenv("PROFILE"),
    System.getenv("GITHUB_TOKEN")
)

val properties = readProperties(environmentVariables.profile)