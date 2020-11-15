package com.ns.adapter.web

import com.ns.core.domain.Client
import com.ns.core.port.`in`.ClientPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class ClientResource(val clientService: ClientPort) : Logging {


    @PostMapping("/client")
    fun createClient(@RequestBody createClientRequest: CreateClientRequest): ResponseEntity<Client> {
        logger.info("Received request  for creating Client!!! $createClientRequest")
        val client = clientService.createClient(client = createClientRequest.toDomainClient())
        return ResponseEntity(client, CREATED)
    }

    @GetMapping("/client")
    fun getAllClients(): List<Client> {
        return clientService.getAllClients()
    }

    @PostMapping("/client/{clientId}/enroll")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun enrollUser(@PathVariable("clientId") clientId: Long) {
        logger.info("Enroll Client $clientId")
        clientService.enrollUser(clientId)

    }

}