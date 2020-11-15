package com.ns.core.port.out

import com.ns.core.domain.Client

interface BankingPort {

    fun createAccount(client: Client)
    fun deleteAccount(client: Client)
}