package http.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.HttpConfig.HTTP
import org.eclipse.jgit.transport.HttpConfig.POST_BUFFER_KEY
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
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
        git.repository.config.setInt(HTTP, null, POST_BUFFER_KEY, 512 * 1024 * 1024)
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
        git.push().call()
    }
}
