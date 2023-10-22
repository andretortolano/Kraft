package com.andretortolano.kraft

interface State {

    fun take(action: Action): Transition?

    fun State.next(state: State, sideEffect: SideEffect? = null) = Transition.Ok(state, sideEffect)

    fun State.keep(error: Error? = null) = if (error != null) {
        Transition.NotOk(this, error)
    } else {
        Transition.Ok(this, null)
    }

    fun State.fatalError(error: Error, state: State) = Transition.NotOk(state, error)
}
