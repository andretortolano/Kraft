package com.andretortolano.kraft.handler

interface ActionHandler {

    suspend fun handle(chain: Chain)

}
