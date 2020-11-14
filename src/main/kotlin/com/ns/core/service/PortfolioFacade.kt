package com.ns.core.service

import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.out.PersistencePort
import com.ns.core.port.out.PortfolioPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.stereotype.Service

@Service
class PortfolioFacade(val portfolioPort: PortfolioPort, val persistencePort: PersistencePort) : Logging {

    @Autowired
    @Lazy
    lateinit var stateMachine: StateMachine<States, Events>
    fun createProfile(clientId: Long) {

        portfolioPort.createProfile(persistencePort.getClientByClientId(clientId))

        val message = MessageBuilder.withPayload(Events.PROFILE_CREATED)
                .setHeader("clientId", clientId)
                .build()
        stateMachine.sendEvent(message)
    }

    fun deleteProfile(clientId: Long) {
        portfolioPort.deleteProfile(persistencePort.getClientByClientId(clientId))
    }

}