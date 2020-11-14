package com.ns.core.service

import com.ns.core.domain.Client
import com.ns.core.port.`in`.ClientPort
import com.ns.core.port.out.PersistencePort
import org.apache.logging.log4j.kotlin.Logging
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