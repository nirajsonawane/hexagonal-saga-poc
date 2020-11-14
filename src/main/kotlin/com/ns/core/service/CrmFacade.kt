package com.ns.core.service

import com.ns.core.domain.ClientStatus
import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.out.CrmPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.stereotype.Service

@Service
class CrmFacade(val crmPort: CrmPort) : Logging {

    @Autowired
    @Lazy
    lateinit var stateMachine: StateMachine<States, Events>
    fun updateStatus(clientId: Long) {
        crmPort.updateClientStatus(clientId, ClientStatus.REGISTERED)
        val message = MessageBuilder.withPayload(Events.CRM_UPDATED)
                .setHeader("clientId", clientId)
                .build()
        stateMachine.sendEvent(message)
    }
}