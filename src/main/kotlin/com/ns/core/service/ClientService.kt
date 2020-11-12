package com.ns.core.service

import com.ns.core.domain.Client
import com.ns.core.domain.ClientStatus
import com.ns.core.port.`in`.ClientPort
import com.ns.core.port.out.BankingPort
import com.ns.core.port.out.PortfolioPort
import com.ns.core.port.out.CrmPort
import com.ns.core.port.out.PersistencePort
import org.springframework.stereotype.Service

@Service
class ClientService(
        val persistencePort: PersistencePort,
        val crmPort: CrmPort,
        val bankingPortOperations: BankingPort,
        val portfolioPort: PortfolioPort
) : ClientPort {


    override fun createClient(client: Client): Client {
        return persistencePort.saveClient(client)
    }

    override fun getAllClients(): List<Client> {
        return persistencePort.getAllClient()
    }

    override fun enrollClient(clientId: Long) {
        val client = persistencePort.getClientByClientId(clientId)
        bankingPortOperations.createAccount(client)
        portfolioPort.createProfile(client)
        crmPort.updateClientStatus(clientId, ClientStatus.REGISTERED)

    }
}