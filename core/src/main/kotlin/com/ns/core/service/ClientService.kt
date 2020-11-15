package com.ns.core.service

import com.ns.core.configuration.StateMachineInMemoryFactory
import com.ns.core.domain.Client
import com.ns.core.domain.Events
import com.ns.core.port.`in`.ClientPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class ClientService(val factory: StateMachineInMemoryFactory, val persistencePort: PersistencePort) : Logging, ClientPort {


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
        factory.getStateMachine(clientId).sendEvent(message)
    }


}