package com.sorrowblue.kdbc

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class PrimaryKey(val autoIncrement: Boolean = false)
