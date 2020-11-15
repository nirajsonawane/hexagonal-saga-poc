package com.ns.core.port.out

import com.ns.core.domain.Client

interface PortfolioPort {

    fun createProfile(client: Client)
    fun deleteProfile(client: Client)
}
