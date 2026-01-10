package api.madn.es

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.Dotenv


@SpringBootApplication
@ConfigurationPropertiesScan
 class MadnessApplication

fun main(args: Array<String>) {

    val isDev =
        System.getProperty("spring.profiles.active") == "dev" ||
                System.getenv("SPRING_PROFILES_ACTIVE") == "dev" ||
                args.any { it.startsWith("--spring.profiles.active=") && it.endsWith("dev") }

    if (isDev) {
        val dotenv = Dotenv.configure()
            .directory("docker/local")
            .filename(".env")
            .ignoreIfMalformed()
            .load()

        // Spring이 읽도록 System property로 주입 (이게 핵심)
        dotenv.entries().forEach { e ->
            System.setProperty(e.key, e.value)
        }

        // 이 한 줄로 “진짜 로드됐는지” 바로 확인 가능
        println("[dotenv] DB_USERNAME=" + System.getProperty("DB_USERNAME"))
        println("[dotenv] DB_URL=" + System.getProperty("DB_URL"))
    }

    runApplication<MadnessApplication>(*args)
}
