package com.dev.sample.data.repository

sealed class ReqError: Exception()
class ServerError(val code: Int): ReqError()
class ServerUnavailable: ReqError()