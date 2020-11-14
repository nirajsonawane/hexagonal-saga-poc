package com.ns.core.service

import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.out.BankingPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.stereotype.Service

@Service
class BankingFacade(val bankingPort: BankingPort, val persistencePort: PersistencePort) : Logging {

    @Autowired
    @Lazy
    lateinit var stateMachine: StateMachine<States, Events>

    fun createAccount(clientId: Long) {
        bankingPort.createAccount(persistencePort.getClientByClientId(clientId))
        val message = MessageBuilder.withPayload(Events.BANK_ACCOUNT_CREATED)
                .setHeader("clientId", clientId)
                .build()
        stateMachine.sendEvent(message)
    }

    fun deleteAccount(clientId: Long) {
        bankingPort.deleteAccount(persistencePort.getClientByClientId(clientId))
    }
}