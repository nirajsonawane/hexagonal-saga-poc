package com.ns.adapter.web

import com.ns.core.domain.Client
import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.`in`.ClientPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.web.bind.annotation.*


@RestController(value = "/enroll")
class EnrollClientResource : Logging {

    @Autowired
    lateinit var stateMachine: StateMachine<States, Events>


    @PostMapping("/{clientId}")
    @ResponseStatus(ACCEPTED)
    fun enrollUser(@PathVariable("clientId") clientId: Long) {
        logger.info("Enroll Client $clientId")
        val message = MessageBuilder.withPayload(Events.STARTED)
                .setHeader("clientId", clientId)
                .build()
        stateMachine.sendEvent(message)

    }
}