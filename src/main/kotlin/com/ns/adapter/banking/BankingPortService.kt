package com.ns.adapter.banking

import com.ns.core.domain.Client
import com.ns.core.port.out.BankingPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class BankingPortService : BankingPort,Logging{
    override fun createAccount(client: Client) {
        logger.info("Creating Account in Banking system for $client")
        //throw RuntimeException("Banking Service Error ")
    }

    override fun deleteAccount(client: Client) {
        logger.info("Deleting Account in Banking system for $client")

    }

}