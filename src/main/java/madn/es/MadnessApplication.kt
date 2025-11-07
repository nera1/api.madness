package madn.es

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MadnessApplication

fun main(args: Array<String>) {
    runApplication<MadnessApplication>(*args)
}
