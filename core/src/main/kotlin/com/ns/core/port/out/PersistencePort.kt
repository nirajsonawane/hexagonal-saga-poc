package com.ns.core.port.out

import com.ns.core.domain.Client

interface PersistencePort{

    fun saveClient(client: Client):Client
    fun getAllClient():List<Client>
    fun getClientByClientId(clientId:Long) :Client
}