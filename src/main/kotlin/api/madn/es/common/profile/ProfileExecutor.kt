package api.madn.es.common.profile

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

object ProfileExecutor {
    private val log = LoggerFactory.getLogger(ProfileExecutor::class.java)
    private lateinit var environment: Environment
    private val profiles: Set<String> by lazy {
        environment.activeProfiles.toSet()
    }

    @Component
    class Initializer(environment: Environment) {
        init {
            ProfileExecutor.environment = environment
        }
    }

    fun execute(block: ProfileExecutorBuilder.() -> Unit) {
        val builder = ProfileExecutorBuilder()
        builder.block()
        builder.executeAll(profiles)
    }


    class ProfileExecutorBuilder {
        private val executors = mutableMapOf<String, () -> Unit>()

        fun devLog(message: String) {
            log.info("[DEV] $message")
        }

        var onDev: (() -> Unit)?
            get() = null
            set(value) {
                value?.let { executors["dev"] = it }
            }

        var onProduction: (() -> Unit)?
            get() = null
            set(value) {
                value?.let { executors["production"] = it }
            }

        var onTest: (() -> Unit)?
            get() = null
            set(value) {
                value?.let { it() }
            }

        fun on(profile: String, block: () -> Unit) {
            executors[profile] = block
        }

        internal fun executeAll(profiles: Set<String>) {
            for (profile in profiles) {
                executors[profile]?.let {
                    it()
                }
            }
        }
    }
}