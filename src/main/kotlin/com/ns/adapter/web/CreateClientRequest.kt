package com.ns.adapter.web

import com.ns.core.domain.Client
import com.ns.core.domain.Country

data class CreateClientRequest(val firstName: String, val lastName: String, val email: String, val mobileNumber: String, val taxNumber: String, val country: Country) {
    fun toDomainClient(): Client {
        return Client(firstName = firstName,
                lastName = lastName,
                email = email,
                mobileNumber = mobileNumber,
                taxNumber = taxNumber,
                country = country
        )
    }
}