package com.ns.core.service

import com.ns.core.configuration.StateMachineInMemoryFactory
import com.ns.core.domain.ClientStatus
import com.ns.core.domain.Events
import com.ns.core.port.out.CrmPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class CrmFacade(val factory: StateMachineInMemoryFactory, val crmPort: CrmPort) : Logging {


    fun updateStatus(clientId: Long) {
        crmPort.updateClientStatus(clientId, ClientStatus.REGISTERED)
        val message = MessageBuilder.withPayload(Events.CRM_UPDATED)
                .setHeader("clientId", clientId)
                .build()
        factory.getStateMachine(clientId).sendEvent(message)
    }
}