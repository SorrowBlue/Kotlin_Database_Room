package com.sorrowblue.kdbc.compiler.ktx

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun TypeName.asClassName() = this as ClassName

val TypeName.kotlinType: TypeName
	get() =
		when {
			toString() == "java.lang.String" -> String::class.asTypeName()
			toString().contains("java.util.List") -> LIST.parameterizedBy((this as ParameterizedTypeName).typeArguments)
			else -> this
		}
