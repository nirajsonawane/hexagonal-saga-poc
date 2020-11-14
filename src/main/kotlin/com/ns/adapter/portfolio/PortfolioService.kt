package com.ns.adapter.portfolio

import com.ns.core.domain.Client
import com.ns.core.port.out.PortfolioPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class PortfolioService : PortfolioPort, Logging {

    override fun createProfile(client: Client) {
        logger.info("Creating Profile :Sending request to Portfolio Service ${CreateProfileRequest.fromDomain(client)}")
        //throw RuntimeException("Exception from Profile Service")

    }

    override fun deleteProfile(client: Client) {
        logger.info("Deleting profile for client ${client.id}")
    }

}