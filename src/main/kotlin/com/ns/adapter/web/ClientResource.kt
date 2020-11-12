package com.ns.adapter.web

import com.ns.core.domain.Client
import com.ns.core.port.`in`.ClientPort
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController(value = "/client")
class ClientResource(val clientService: ClientPort) : Logging {


    @PostMapping
    fun createClient(@RequestBody createClientRequest: CreateClientRequest): ResponseEntity<Client> {
        logger.info("Received request  for creating Client!!! $createClientRequest")
        val client = clientService.createClient(client = createClientRequest.toDomainClient())
        return ResponseEntity(client, CREATED)
    }

    @GetMapping
    fun getAllClients(): List<Client> {
        logger.info("test ")
        return clientService.getAllClients()
    }
    @PostMapping("/{clientId}/enroll")
    @ResponseStatus(ACCEPTED)
    fun enrollUser(@PathVariable("clientId") clientId :Long){
        logger.info("Enroll Client $clientId")
            clientService.enrollClient(clientId)
    }
}