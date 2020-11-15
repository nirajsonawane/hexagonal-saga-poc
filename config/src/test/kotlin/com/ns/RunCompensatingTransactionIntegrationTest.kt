package com.ns

import com.ns.adapter.banking.BankingPortService
import com.ns.adapter.crm.CrmPortClientService
import com.ns.adapter.persistence.ClientRepository
import com.ns.adapter.persistence.entity.Client
import com.ns.adapter.portfolio.PortfolioService
import com.ns.core.configuration.StateMachineInMemoryFactoryFactory
import com.ns.core.domain.ClientStatus
import com.ns.core.domain.Country
import com.ns.core.domain.States
import org.apache.logging.log4j.kotlin.Logging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class RunCompensatingTransactionIntegrationTest : Logging {


    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var stateMachineInMemoryFactoryFactory: StateMachineInMemoryFactoryFactory

    @Autowired
    lateinit var clientRepository: ClientRepository

    @MockBean
    lateinit var portfolioService: PortfolioService

    @MockBean
    lateinit var bankingPortService: BankingPortService

    @MockBean
    lateinit var crmPortService:CrmPortClientService

    @BeforeEach
    fun setup() {
        stateMachineInMemoryFactoryFactory.cleanUp()
        clientRepository.deleteAll()
        val client = Client(clientId = null, firstName = "Test", lastName = "", email = "", mobileNumber = "", taxNumber = "", country = Country.DE)
        clientRepository.save(client)

    }

    @Nested
    inner class BankingPort{

        @Test
        fun `Account Enrollment Saga should end with Account Creation Error state if Banking port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.`when`(bankingPortService.createAccount(client = client)).thenThrow(RuntimeException("Mock Exception"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted)

            assertEquals(States.BANK_ACCOUNT_CREATION_ERROR.name, stateMachineInMemoryFactoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }

    @Nested
    inner class PortfolioPort{

        @Test
        fun `Account Enrollment Saga should end with Profile Creation Error state if Portfolio port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.doNothing().`when`(bankingPortService).createAccount(client = client)
            Mockito.`when`(portfolioService.createProfile(client)).thenThrow(RuntimeException("Mock Exception from portfolio Service"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted);

            assertEquals(States.PROFILE_CREATION_ERROR.name, stateMachineInMemoryFactoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }

    @Nested
    inner class CRMPort{

        @Test
        fun `Account Enrollment Saga should end with Profile Creation Error state if CRM port throws exception`() {
            val client = clientRepository.findAll()[0].toDomain()
            Mockito.doNothing().`when`(bankingPortService).createAccount(client = client)
            Mockito.doNothing().`when`(portfolioService).createProfile(client = client)
            Mockito.`when`(crmPortService.updateClientStatus(client.id!!,ClientStatus.REGISTERED)).thenThrow(RuntimeException("Mock Exception from CRM  Port"))
            mockMvc
                    .perform(post("/client/${client.id}/enroll"))
                    .andExpect(status().isAccepted);

            assertEquals(States.CRM_UPDATE_ERROR.name, stateMachineInMemoryFactoryFactory.getStateMachine(client.id!!).state.id.name)
        }
    }


}
