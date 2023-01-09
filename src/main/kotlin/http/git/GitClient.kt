package http.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.lang.Exception
import java.time.LocalDateTime

class GitClient(repoPath: String, password: String) {

    private val git = Git.open(File(repoPath))

    init {
        CredentialsProvider.setDefault(
            UsernamePasswordCredentialsProvider(
                "finance-backend",
                password
            )
        )
    }

    fun sync() {
        addAllFiles()
        commit()
        pull()
        push()
    }

    private fun addAllFiles() {
        git.add().addFilepattern(".").call()
    }

    private fun commit() {
        git.commit().setMessage(LocalDateTime.now().toString()).call()
    }

    private fun pull() {
        git.pull().call()
    }

    private fun push() {
        try {
            git.push().call()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
