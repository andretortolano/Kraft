package com.andretortolano.kraft

import com.andretortolano.kraft.effects.SideEffectLauncher
import com.andretortolano.kraft.handler.ActionHandler
import com.andretortolano.kraft.handler.Chain
import com.andretortolano.kraft.handler.InternalActionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

open class Kraft(
    initialState: State,
    private val actionHandler: ActionHandler = InternalActionHandler(),
    private val launcher: SideEffectLauncher = SideEffectLauncher(),
) {

    private val _stateFlow = MutableSharedFlow<State>(1).apply { onEmpty { emit(initialState) } }
    val stateFlow: SharedFlow<State>
        get() = _stateFlow

    private val _errorFlow = MutableSharedFlow<Error>()
    val errorFlow: SharedFlow<Error>
        get() = _errorFlow

    private var _currentState: State = initialState
    val currentState: State
        get() = _currentState

    private val mutex: Mutex = Mutex()

    suspend fun handle(action: Action) = mutex.withLock {
        val chain = Chain(this, action)

        CoroutineScope(Dispatchers.Default).launch {
            actionHandler.handle(chain)
        }

        chain.begin()

        handleChainedAction(action, chain)
    }


    private suspend fun handleChainedAction(action: Action, chain: Chain): State {
        val transition = _currentState.take(action) ?: chain.defaultTransition(_currentState, action)

        transition.process(chain)

        return _currentState
    }

    private suspend fun Transition.process(chain: Chain) {
        _currentState = state
        // emit new state
        _stateFlow.emit(state)
        // emit error if present
        when (this) {
            is Transition.NotOk -> _errorFlow.emit(error)
            is Transition.Ok -> if (sideEffect != null) {
                launcher.launch(sideEffect)?.let { nextAction -> handleChainedAction(nextAction, chain) }
            }
        }
    }
}
