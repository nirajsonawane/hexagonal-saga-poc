package com.ns.core.port.out

import com.ns.core.domain.ClientStatus

interface CrmPort {

    fun updateClientStatus(clientId:Long,clientStatus: ClientStatus)
}