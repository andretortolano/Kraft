package com.andretortolano.kraft

sealed class Transition(
    val state: State,
    open val error: Error?,
    val sideEffect: SideEffect?
) {
    class Ok(
        state: State,
        sideEffect: SideEffect?
    ) : Transition(state, null, sideEffect)

    class NotOk(
        state: State,
        override val error: Error
    ) : Transition(state, error, null)
}
