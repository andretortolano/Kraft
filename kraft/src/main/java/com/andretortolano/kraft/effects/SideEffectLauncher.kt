package com.andretortolano.kraft.effects

import com.andretortolano.kraft.Action
import com.andretortolano.kraft.SideEffect

open class SideEffectLauncher {

    open suspend fun launch(sideEffect: SideEffect): Action? {
        if (sideEffect is DefaultSideEffect) {
            return sideEffect.launch()
        }

        return null
    }
}
