package com.sorrowblue.kdbr

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(val sql: String, val anyList: Boolean = false)
