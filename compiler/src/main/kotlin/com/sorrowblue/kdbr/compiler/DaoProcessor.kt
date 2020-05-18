package com.sorrowblue.kdbr.compiler

import com.google.auto.service.AutoService
import com.sorrowblue.kdbr.*
import com.sorrowblue.kdbr.compiler.ktx.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.sql.Connection
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
class DaoProcessor : AbstractProcessor() {
	companion object {
		const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
	}

	override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Dao::class.java.name)

	override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

	override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
		roundEnv.getElementsAnnotatedWith(Dao::class.java).forEach {
			if (it.kind != ElementKind.INTERFACE) {
				processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only interfaces can be annotated")
				return true
			}
			processAnnotation(it)
		}
		return false
	}

	private fun processAnnotation(element: Element) {
		val className = element.simpleName.toString()
		val packageName = processingEnv.elementUtils.getPackageOf(element).toString() + ".room"
		val fileName = "${className}_Imp"

		val fileBuilder = FileSpec.builder(packageName, fileName).indent("\t")

		val classBuilder = TypeSpec.classBuilder(fileName)
			.superclass(RoomDao::class.java)
			.addSuperclassConstructorParameter("connection")
			.addSuperinterface(element.asType().asTypeName())
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter("connection", Connection::class.java)
					.build()
			)
		element.enclosedElements.forEach { classBuilder.addFunction(sqlMethod(it as ExecutableElement)) }
		fileBuilder.addType(classBuilder.build()).build()
			.writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!))
	}

	private fun sqlMethod(element: ExecutableElement) =
		FunSpec.builder(element.simpleName.toString())
			.addModifiers(KModifier.OVERRIDE).apply {
				element.getAnnotation(Query::class.java)?.let { queryMethod(it, element) }
					?: element.getAnnotation(Insert::class.java)?.let { insertMethod(it, element) }
					?: element.getAnnotation(Delete::class.java)?.let { deleteMethod(element) }
					?: element.getAnnotation(Update::class.java)?.let { updateMethod(element) }
			}.build()

	private fun FunSpec.Builder.insertMethod(insert: Insert, element: ExecutableElement) {
		element.parameters.firstOrNull()?.let {
			if ((it.asType().asTypeName() as? ParameterizedTypeName)?.rawType?.toString() == "kotlin.Array") {
				val className = ClassName(
					it.asType().genericType.asClassName().packageName + ".room",
					it.asType().genericType.asClassName().simpleName + "_RoomEntity"
				)
				addParameter(it.toString(), it.asType().genericType.kotlinType, KModifier.VARARG)
				addStatement(
					"var sql = %P",
					CodeBlock.builder().add("INSERT INTO \${%T.tableName} (", className).build()
				)
				addStatement(
					"val cn = if (%1T.isAutoIncrementEnabled) %1T.primaryLessColumnNames else %1T.columnNames",
					className
				)
				addStatement("sql += cn.joinToString(%S)", ",")
				addStatement(
					"sql += %P;",
					CodeBlock.builder().add(
						") VALUES \${$it.map { %1T.values(it).joinToString(%2S) { %3S } }.joinToString(%2S) {\"(\$it)\"}}",
						className,
						",",
						"?"
					)
						.build()
				)
				addStatement("println(sql)")
				addStatement("val statement = connection.prepareStatement(sql)")
				addStatement(
					"statement.%M(*$it.flatMap { %T.values(it) }.toTypedArray())",
					MemberName("com.sorrowblue.kdbr.ktx", "setAll"),
					className
				)
			} else if (it.asType().isCollection) {
				val className = ClassName(
					it.asType().genericType.asClassName().packageName + ".room",
					it.asType().genericType.asClassName().simpleName + "_RoomEntity"
				)
				addParameter(it.toString(), it.asType().asTypeName().kotlinType)
				addStatement(
					"var sql = %P",
					CodeBlock.builder().add("INSERT INTO \${%T.tableName} (", className).build()
				)
				addStatement(
					"val cn = if (%1T.isAutoIncrementEnabled) %1T.primaryLessColumnNames else %1T.columnNames",
					className
				)
				addStatement("sql += cn.joinToString(%S)", ",")
				addStatement(
					"sql += %P;",
					CodeBlock.builder().add(
						") VALUES \${$it.map { %1T.values(it).joinToString(%2S) { %3S } }.joinToString(%2S) {\"(\$it)\"}}",
						className,
						",",
						"?"
					)
						.build()
				)
				addStatement("println(sql)")
				addStatement("val statement = connection.prepareStatement(sql)")
				addStatement(
					"statement.%M(*$it.flatMap { %T.values(it) }.toTypedArray())",
					MemberName("com.sorrowblue.kdbr.ktx", "setAll"),
					className
				)
			} else {
				val className = ClassName(
					it.asType().asTypeName().asClassName().packageName + ".room",
					it.asType().asTypeName().asClassName().simpleName + "_RoomEntity"
				)
				addParameter(it.toString(), it.asType().asTypeName())
				addStatement(
					"var sql = %P",
					CodeBlock.builder().add("INSERT INTO \${%T.tableName} (", className).build()
				)
				addStatement(
					"val cn = if (%1T.isAutoIncrementEnabled) %1T.primaryLessColumnNames else %1T.columnNames",
					className
				)
				addStatement("sql += cn.joinToString(%S)", ",")
				addStatement(
					"sql += %P;",
					CodeBlock.builder().add(") VALUES(\${%T.values($it).joinToString(%S) { %S } })", className, ",", "?")
						.build()
				)
				addStatement("println(sql)")
				addStatement("val statement = connection.prepareStatement(sql)")
				addStatement(
					"statement.%M(*%T.values($it).toTypedArray())",
					MemberName("com.sorrowblue.kdbr.ktx", "setAll"),
					className
				)
			}
		}
		val isReturn = if (element.returnType.isInt) {
			returns(Int::class.java)
			"return"
		} else ""
		addStatement("$isReturn statement.%M()", MemberName("com.sorrowblue.kdbr.ktx", "update"))
	}

	private fun FunSpec.Builder.updateMethod(element: ExecutableElement) {
		element.parameters.firstOrNull()?.let {
			val className = ClassName(
				it.asType().asTypeName().asClassName().packageName + ".room",
				it.asType().asTypeName().asClassName().simpleName + "_RoomEntity"
			)
			addParameter(it.toString(), it.asType().asTypeName())
			addStatement(
				"val sql = %P",
				CodeBlock.builder().add(
					"UPDATE \${%1T.tableName} SET \${%1T.primaryLessColumnNames.joinToString(%2S) { %3P }} WHERE \${%1T.primaryColumn} = ?;",
					className,
					",",
					"\$it = ?"
				).build()
			)
			addStatement("println(sql)")
			addStatement("val statement = connection.prepareStatement(sql)")
			addStatement(
				"statement.%1M(*%2T.primaryLessValues($it).plus(%2T.primaryValue($it)).toTypedArray())",
				MemberName("com.sorrowblue.kdbr.ktx", "setAll"), className
			)
			val isReturn = if (element.returnType.isInt) {
				returns(Int::class.java)
				"return"
			} else ""
			addStatement("$isReturn statement.%M()", MemberName("com.sorrowblue.kdbr.ktx", "update"))
		}
	}

	private fun FunSpec.Builder.deleteMethod(element: ExecutableElement) {
		element.parameters.firstOrNull()?.let {
			addParameter(it.toString(), it.asType().asTypeName())
			val className = ClassName(
				it.asType().asTypeName().asClassName().packageName + ".room",
				it.asType().asTypeName().asClassName().simpleName + "_RoomEntity"
			)
			addStatement(
				"val sql = %P",
				CodeBlock.builder().add("DELETE from \${%1T.tableName} WHERE \${%1T.primaryColumn} = ?;", className)
					.build()
			)
			addStatement("println(sql)")
			addStatement("val statement = connection.prepareStatement(sql)")
			addStatement("statement.setObject(1, %T.primaryValue(%L))", className, it.toString())
		}
		val isReturn = if (element.returnType.isInt) {
			returns(Int::class.java)
			"return"
		} else ""
		addStatement("$isReturn statement.%M()", MemberName("com.sorrowblue.kdbr.ktx", "update"))

	}

	private fun FunSpec.Builder.queryMethod(query: Query, element: ExecutableElement) {
		val returnClass =
			(if (element.returnType.isCollection) element.returnType.genericType else element.returnType.asTypeName()).asClassName()
		val returnType: TypeMirror = element.returnType
		val entityClass = ClassName("${returnClass.packageName}.room", "${returnClass.simpleName}_RoomEntity")
		element.parameters.forEach { addParameter(it.toString(), it.asType().asTypeName().kotlinType) }
		val regex = Regex(":\\w+?\\b")
		addStatement("val sql = %S", regex.replace(query.sql, "?"))
		addStatement("println(sql)")
		addStatement("val statement = connection.prepareStatement(sql)")
		statementSetAllCodeBlock(query.sql, element)?.let { addCode(it) }
		if (query.anyList) {
			addStatement("var index = 1")
			addStatement(
				"return statement.%M { it.%M { it.getObject(index++) } }",
				MemberName("com.sorrowblue.kdbr.ktx", "query"),
				MemberName("com.sorrowblue.kdbr.ktx", "map")
			)
			returns(LIST.parameterizedBy(Any::class.asTypeName().copy(true)))
		} else {
			addStatement(
				"return statement.%M { it.%M(%T::result) }${if (returnType.isCollection) "" else ".firstOrNull()"}",
				MemberName("com.sorrowblue.kdbr.ktx", "query"),
				MemberName("com.sorrowblue.kdbr.ktx", "map"),
				entityClass
			)
			returns(
				if (returnType.isCollection) element.returnType.asTypeName().kotlinType else element.returnType.asTypeName()
					.copy(nullable = true)
			)
		}
	}

	private fun statementSetAllCodeBlock(sql: String, method: ExecutableElement): CodeBlock? {
		val regex = Regex(":\\w+?\\b")
		val arguments = method.parameters.map(VariableElement::toString)
		val placeHolders = regex.findAll(sql).map { it.value.substring(1, it.value.length) }.toList()
		if (placeHolders.isEmpty()) {
			return null
		}
		placeHolders.forEach {
			if (!arguments.contains(it)) {
				processingEnv.messager.printMessage(
					Diagnostic.Kind.ERROR,
					"The method '${method.simpleName}' argument does not match SQL statement placeholder($it)."
				)
			}
		}
		return CodeBlock.builder().add(
			"statement.%M(%L)\n",
			MemberName("com.sorrowblue.kdbr.ktx", "setAll"),
			placeHolders.joinToString(",")
		).build()
	}
}
