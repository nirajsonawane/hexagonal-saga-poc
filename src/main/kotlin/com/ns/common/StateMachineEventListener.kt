package com.ns.common

import com.ns.core.domain.Events
import com.ns.core.domain.States
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.messaging.Message
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition

class StateMachineEventListener :Logging, StateMachineListenerAdapter<States, Events>() {
    override fun stateChanged(from: State<States, Events>?, to: State<States, Events>?) {
        logger.info("State Changed from ${from?.id} To ${to?.id}")
    }
    override fun stateEntered(state: State<States, Events>) {}
    override fun stateExited(state: State<States, Events>) {}
    override fun transition(transition: Transition<States, Events>) {}
    override fun transitionStarted(transition: Transition<States, Events>) {}
    override fun transitionEnded(transition: Transition<States, Events>) {}
    override fun stateMachineStarted(stateMachine: StateMachine<States, Events>) {}
    override fun stateMachineStopped(stateMachine: StateMachine<States, Events>) {}
    override fun eventNotAccepted(event: Message<Events>) {}
    override fun extendedStateChanged(key: Any, value: Any) {}
    override fun stateMachineError(stateMachine: StateMachine<States, Events>, exception: Exception) {}
    override fun stateContext(stateContext: StateContext<States, Events>) {}
}