package api.madn.es

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.Dotenv


@SpringBootApplication
@ConfigurationPropertiesScan
 class MadnessApplication

fun main(args: Array<String>) {
    runApplication<MadnessApplication>(*args)
}
