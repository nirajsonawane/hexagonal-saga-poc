package com.ns.core.configuration

import com.ns.core.domain.Events
import com.ns.core.domain.States
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.context.annotation.Lazy
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class StateMachineInMemoryFactoryFactory(@Lazy val stateMachineFactory: StateMachineFactory<States, Events>) : Logging {


    val stateMachineMap = HashMap<Long, StateMachine<States, Events>>()

    fun getStateMachine(machineId: Long): StateMachine<States, Events> {
        stateMachineMap.putIfAbsent(machineId, stateMachineFactory.getStateMachine(machineId.toString()))
        return stateMachineMap[machineId] ?: throw RuntimeException("Not able to create to state machine")
    }

    fun cleanUp() {
        stateMachineMap.clear()
    }

}