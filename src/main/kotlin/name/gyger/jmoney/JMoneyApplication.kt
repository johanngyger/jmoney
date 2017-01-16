package name.gyger.jmoney

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class JMoneyApplication

fun main(args: Array<String>) {
    SpringApplication.run(JMoneyApplication::class.java, *args)
}
