package finance.http.git

import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class GitClient(repoPath: String) {

    private val file = File(repoPath)

    fun sync() {
        "git status".runCommand()
        "git add .".runCommand()
        "git commit -m \"${LocalDateTime.now()}\"".runCommand()
        "git pull".runCommand()
        "git push".runCommand()
    }

    private fun String.runCommand() {
        ProcessBuilder(*split(" ").toTypedArray())
            .directory(file)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
    }
}