package com.ns.adapter.portfolio

import com.ns.core.domain.Client

data class CreateProfileRequest(val firstName: String, val lastName: String, val taxNumber: String) {
    companion object {
        fun fromDomain(client: Client): CreateProfileRequest {
            return CreateProfileRequest(firstName = client.firstName, lastName = client.lastName, taxNumber = client.taxNumber)
        }
    }
}