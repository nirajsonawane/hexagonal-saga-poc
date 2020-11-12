package com.ns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HexagonalSagaApplication

fun main(args: Array<String>) {
	runApplication<HexagonalSagaApplication>(*args)
}
