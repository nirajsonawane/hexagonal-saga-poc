package com.ns.core.service

import com.ns.core.configuration.StateMachineInMemoryFactoryFactory
import com.ns.core.domain.Events
import com.ns.core.port.out.BankingPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class BankingFacade(val factory: StateMachineInMemoryFactoryFactory, val bankingPort: BankingPort, val persistencePort: PersistencePort) : Logging {


    fun createAccount(clientId: Long) {
        bankingPort.createAccount(persistencePort.getClientByClientId(clientId))
        val message = MessageBuilder.withPayload(Events.BANK_ACCOUNT_CREATED)
                .setHeader("clientId", clientId)
                .build()
        factory.getStateMachine(clientId).sendEvent(message)
    }

    fun deleteAccount(clientId: Long) {
        bankingPort.deleteAccount(persistencePort.getClientByClientId(clientId))
    }
}