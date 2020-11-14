package com.ns.core.service

import com.ns.core.domain.Client
import com.ns.core.domain.ClientStatus
import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.port.`in`.ClientPort
import com.ns.core.port.out.BankingPort
import com.ns.core.port.out.PortfolioPort
import com.ns.core.port.out.CrmPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import org.springframework.stereotype.Service

@Service
class ClientService( val persistencePort: PersistencePort) : Logging, ClientPort {

    override fun createClient(client: Client): Client {
        return persistencePort.saveClient(client)
    }

    override fun getAllClients(): List<Client> {
        return persistencePort.getAllClient()
    }


}