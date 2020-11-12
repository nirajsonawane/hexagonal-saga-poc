package com.ns.adapter.persistence

import com.ns.core.domain.Client
import com.ns.core.port.out.PersistencePort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class ClientPersistencePortAdapter(val clientRepository: ClientRepository) : PersistencePort {
    override fun saveClient(client: Client): Client {
        val clientEntity = clientRepository.save(com.ns.adapter.persistence.entity.Client.fromDomain(client))
        return clientEntity.toDomain()
    }

    override fun getAllClient(): List<Client> {
        val clientList = clientRepository.findAll()
        return clientList.map { it.toDomain() }
    }

    override fun getClientByClientId(clientId: Long) :Client  {
        val client = clientRepository.findByIdOrNull(clientId)
                ?: throw  RuntimeException("Client not found for clientId $clientId")
        return client.toDomain()
    }
}