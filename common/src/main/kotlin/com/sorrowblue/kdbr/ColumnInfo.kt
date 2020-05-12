package com.sorrowblue.kdbr

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class ColumnInfo(val name: String)
