package unit

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

class Config : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerTest
}
