package com.sorrowblue.kdbr

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Entity(val tableName: String)
