package com.ns.common

import com.ns.core.domain.Events
import com.ns.core.domain.States
import com.ns.core.service.BankingFacade
import com.ns.core.service.CrmFacade
import com.ns.core.service.PortfolioFacade
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer


@Configuration
@EnableStateMachine
class AccountEnrollmentSagaConfigurations : EnumStateMachineConfigurerAdapter<States, Events>(), Logging {

    @Autowired
    lateinit var portfolioFacade: PortfolioFacade

    @Autowired
    lateinit var bankingFacade: BankingFacade

    @Autowired
    lateinit var crmFacade: CrmFacade


    @Throws(Exception::class)
    override fun configure(states: StateMachineStateConfigurer<States, Events>) {
        states
                .withStates()
                .initial(States.STARTED)
                .stateEntry(States.BANK_ACCOUNT_CREATION_INITIATED, createAccountAction(),errorAction())
                .stateEntry(States.BANK_ACCOUNT_CREATED, createProfileAction(),errorActionProfileError())
                .state(States.PROFILE_CREATED, updateCrmAction())
                .state(States.CRM_UPDATED)
                .state(States.BANK_ACCOUNT_CREATION_ERROR,runCompensatingTransaction())
                .state(States.PROFILE_CREATION_ERROR,runCompensatingTransaction())
                .end(States.CRM_UPDATED)
                .end(States.BANK_ACCOUNT_CREATION_ERROR)
                .end(States.PROFILE_CREATION_ERROR)



    }

    @Throws(java.lang.Exception::class)
    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>) {
        config.withConfiguration()
                .listener(StateMachineEventListener())
                .autoStartup(true)
    }

    @Throws(java.lang.Exception::class)
    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>) {
        transitions
                .withExternal()
                .source(States.STARTED).target(States.BANK_ACCOUNT_CREATION_INITIATED).event(Events.STARTED)
                .and()
                .withExternal()
                .source(States.BANK_ACCOUNT_CREATION_INITIATED).target(States.BANK_ACCOUNT_CREATED).event(Events.BANK_ACCOUNT_CREATED)
                .and()
                .withExternal()
                .source(States.BANK_ACCOUNT_CREATION_INITIATED).target(States.BANK_ACCOUNT_CREATION_ERROR).event(Events.BANK_ACCOUNT_CREATION_ERROR)
                .and()
                .withExternal()
                .source(States.BANK_ACCOUNT_CREATED).target(States.PROFILE_CREATED).event(Events.PROFILE_CREATED)
                .and()
                .withExternal()
                .source(States.BANK_ACCOUNT_CREATED).target(States.PROFILE_CREATION_ERROR).event(Events.PROFILE_CREATION_ERROR)
                .and()
                .withExternal()
                .source(States.PROFILE_CREATED).target(States.CRM_UPDATED).event(Events.CRM_UPDATED)


    }

    @Bean
    fun createAccountAction(): Action<States, Events> {
        return Action<States, Events> {
            logger.info("Current State ${it.stateMachine.state.id}")
            bankingFacade.createAccount(it.message.headers["clientId"] as Long)

        }
    }

    @Bean
    fun runCompensatingTransaction(): Action<States, Events> {
        return Action<States, Events> {
            bankingFacade.deleteAccount(it.message.headers["clientId"] as Long)
            portfolioFacade.deleteProfile(it.message.headers["clientId"] as Long)

        }
    }

    @Bean
    fun errorAction(): Action<States?, Events?>? {
        return Action { context ->

            logger.error("Error Action........")
            logger.error(context.stateMachine.state.id!!)
            context.exception.message?.let { "Error in${context.stateMachine.state.id }${logger.error(it)}" }
            val message = MessageBuilder.withPayload(Events.BANK_ACCOUNT_CREATION_ERROR)
                    .setHeader("clientId", context.messageHeaders["clientId"] as Long)
                    .build()
            context.stateMachine.sendEvent(message)

        }
    }
    @Bean
    fun errorActionProfileError(): Action<States?, Events?>? {
        return Action { context ->
            logger.error("Error Action........")
            logger.error(context.stateMachine.state.id!!)
            context.exception.message?.let { "Error in${context.stateMachine.state.id }${logger.error(it)}" }
            val message = MessageBuilder.withPayload(Events.PROFILE_CREATION_ERROR)
                    .setHeader("clientId", context.messageHeaders["clientId"] as Long)
                    .build()
            context.stateMachine.sendEvent(message)
            logger.info("After Sending event!!")
            logger.error(context.stateMachine.state.id!!)

        }
    }


    @Bean
    fun createProfileAction(): Action<States, Events> {
        return Action<States, Events> {
            portfolioFacade.createProfile(it.message.headers["clientId"] as Long)

        }
    }

    @Bean
    fun updateCrmAction(): Action<States, Events> {
        return Action<States, Events> {
            crmFacade.updateStatus(it.message.headers["clientId"] as Long)
        }
    }


}