package com.ns.core.port.`in`

import com.ns.core.domain.Client

interface ClientPort {

    fun createClient(client: Client): Client

    fun getAllClients(): List<Client>

    fun enrollClient(clientId: Long)

}