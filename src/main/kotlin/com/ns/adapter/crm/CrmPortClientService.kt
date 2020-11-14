package com.ns.adapter.crm

import com.ns.core.domain.ClientStatus
import com.ns.core.port.out.CrmPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.RuntimeException

@Service
class CrmPortClientService : CrmPort,Logging{

    override fun updateClientStatus(clientId:Long,clientStatus: ClientStatus) {
        logger.info("Updating Client Status in CRM Application")
        throw RuntimeException("CRM Service Error")
    }

}