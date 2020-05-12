package com.sorrowblue.kdbr

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class PrimaryKey(val autoIncrement: Boolean = false)
