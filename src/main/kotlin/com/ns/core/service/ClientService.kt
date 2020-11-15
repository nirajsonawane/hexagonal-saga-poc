package com.ns.core.service

import com.ns.core.domain.Client
import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.`in`.ClientPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.stereotype.Service

@Service
class ClientService(val persistencePort: PersistencePort) : Logging, ClientPort {

    @Autowired
    lateinit var stateMachine: StateMachine<States, Events>
    override fun createClient(client: Client): Client {
        return persistencePort.saveClient(client)
    }

    override fun getAllClients(): List<Client> {
        return persistencePort.getAllClient()
    }

    override fun enrollUser(clientId: Long) {
        val message = MessageBuilder.withPayload(Events.STARTED)
                .setHeader("clientId", clientId)
                .build()
        stateMachine.sendEvent(message)
    }


}