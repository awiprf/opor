package com.opor.aut.sgu.usn.vec

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.opor.aut.sgu.usn.vec", "com.opor.lib.cfg"])
class VecApplication

fun main(args: Array<String>) {
    runApplication<VecApplication>(*args)
}
