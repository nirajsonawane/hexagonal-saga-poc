package com.ns.core.service

import com.ns.core.configuration.StateMachineInMemoryFactoryFactory
import com.ns.core.domain.Events
import com.ns.core.port.out.PersistencePort
import com.ns.core.port.out.PortfolioPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class PortfolioFacade(val factory: StateMachineInMemoryFactoryFactory, val portfolioPort: PortfolioPort, val persistencePort: PersistencePort) : Logging {


    fun createProfile(clientId: Long) {

        portfolioPort.createProfile(persistencePort.getClientByClientId(clientId))

        val message = MessageBuilder.withPayload(Events.PROFILE_CREATED)
                .setHeader("clientId", clientId)
                .build()
        factory.getStateMachine(clientId).sendEvent(message)
    }

    fun deleteProfile(clientId: Long) {
        portfolioPort.deleteProfile(persistencePort.getClientByClientId(clientId))
    }

}