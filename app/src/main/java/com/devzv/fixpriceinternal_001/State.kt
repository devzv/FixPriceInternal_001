package com.devzv.fixpriceinternal_001

sealed class State
object Default: State()
object Loading: State()
class Error(val throwable: Throwable): State()
class Complete(val msg: String): State()
