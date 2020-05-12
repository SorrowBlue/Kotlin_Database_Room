package com.sorrowblue.kdbr.compiler.ktx

import com.google.auto.common.MoreTypes
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.type.TypeMirror

val TypeMirror.isCollection
	get() = MoreTypes.isType(this) &&
			(MoreTypes.isTypeOf(java.util.List::class.java, this) ||
					MoreTypes.isTypeOf(java.util.Set::class.java, this))
val TypeMirror.isInt
	get() = MoreTypes.isType(this) && MoreTypes.isTypeOf(Int::class.java, this)

val TypeMirror.genericType get() = (asTypeName() as ParameterizedTypeName).typeArguments.first()


