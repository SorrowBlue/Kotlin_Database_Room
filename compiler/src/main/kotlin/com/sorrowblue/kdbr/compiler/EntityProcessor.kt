package com.sorrowblue.kdbr.compiler

import com.google.auto.service.AutoService
import com.sorrowblue.kdbr.ColumnInfo
import com.sorrowblue.kdbr.Entity
import com.sorrowblue.kdbr.PrimaryKey
import com.sorrowblue.kdbr.RoomEntity
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.sql.ResultSet
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

private val <E : Element> List<E>.fields get() = mapNotNull { if (it.kind.isField) it else null }

@AutoService(Processor::class)
class EntityProcessor : AbstractProcessor() {
	companion object {
		const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
	}

	override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Entity::class.java.name)

	override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

	override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
		roundEnv.getElementsAnnotatedWith(Entity::class.java).forEach {
			if (it.kind != ElementKind.CLASS) {
				processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
				return true
			}
			processAnnotation(it)
		}
		return false
	}

	private fun processAnnotation(element: Element) {
		val className = "${element.simpleName}_RoomEntity"
		val packageName = "${processingEnv.elementUtils.getPackageOf(element)}.room"
		val fieldNames = element.enclosedElements.mapNotNull { if (it.kind.isField) it.simpleName.toString() else null }
		val columnNames = element.enclosedElements.mapNotNull {
			if (it.kind.isField) it.getAnnotation(ColumnInfo::class.java)?.name ?: it.simpleName.toString() else null
		}

		val fileBuilder = FileSpec.builder(packageName, className)
		val objectBuilder = TypeSpec.objectBuilder(className)

		objectBuilder.addProperty(
			PropertySpec.builder(RoomEntity<*, *>::tableName.name, String::class, KModifier.OVERRIDE)
				.initializer("\"${element.getAnnotation(Entity::class.java).tableName}\"").build()
		)
		objectBuilder.addProperty(
			PropertySpec.builder(
				RoomEntity<*, *>::columnNames.name,
				LIST.parameterizedBy(String::class.asTypeName()),
				KModifier.OVERRIDE
			)
				.initializer("listOf(${columnNames.joinToString(",") { "\"${it}\"" }})")
				.build()
		)

		element.enclosedElements.fields.forEach {
			it.getAnnotation(PrimaryKey::class.java)?.let { pk ->
				val primaryKeyType = it.asType().asTypeName()
				val primaryKey = it.simpleName.toString()
				val columnName = it.getAnnotation(ColumnInfo::class.java)?.name ?: it.simpleName.toString()
				objectBuilder.addProperty(
					PropertySpec.builder(RoomEntity<*, *>::primaryColumn.name, String::class.asTypeName())
						.addModifiers(KModifier.OVERRIDE)
						.initializer("\"$columnName\"")
						.build()
				)
				objectBuilder.addFunction(valuesFunction(element, fieldNames, pk.autoIncrement, primaryKey))
				objectBuilder.addFunction(primaryLessValuesFunction(element, fieldNames, pk.autoIncrement, primaryKey))
				objectBuilder.addProperty(isAutoIncrementEnabled(pk.autoIncrement))
				objectBuilder.addFunction(primaryValueFunction(element, primaryKeyType, primaryKey))
				objectBuilder.addSuperinterface(
					RoomEntity::class.asClassName().parameterizedBy(element.asType().asTypeName(), primaryKeyType)
				)
				return@forEach
			}
		}


		objectBuilder.addFunction(resultFunction(element, columnNames))
		fileBuilder.addType(objectBuilder.build())
		fileBuilder.addImport("com.sorrowblue.kdbr.ktx", "get")
		fileBuilder.build().writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!))
	}

	private fun valuesFunction(element: Element, fieldNames: List<String>, autoIncrement: Boolean, primaryKey: String) =
		FunSpec.builder(RoomEntity<*, *>::values.name).addModifiers(KModifier.OVERRIDE)
			.addParameter("entity", element.asType().asTypeName())
			.returns(LIST.parameterizedBy(Any::class.asTypeName().copy(nullable = true)))
			.addStatement(
				"return listOf(%L)",
				(if (autoIncrement) fieldNames.dropWhile { it == primaryKey } else fieldNames).joinToString(",") { "entity.$it" }
			).build()


	private fun primaryLessValuesFunction(element: Element, fieldNames: List<String>, autoIncrement: Boolean, primaryKey: String) =
		FunSpec.builder(RoomEntity<*, *>::primaryLessValues.name).addModifiers(KModifier.OVERRIDE)
			.addParameter("entity", element.asType().asTypeName())
			.returns(LIST.parameterizedBy(Any::class.asTypeName().copy(nullable = true)))
			.addStatement(
				"return listOf(%L)",
				(fieldNames.dropWhile { it == primaryKey }).joinToString(",") { "entity.$it" }
			).build()


	private fun isAutoIncrementEnabled(autoIncrement: Boolean) =
		PropertySpec.builder(RoomEntity<*, *>::isAutoIncrementEnabled.name, Boolean::class, KModifier.OVERRIDE)
			.initializer(if (autoIncrement) "true" else "false").build()

	private fun primaryValueFunction(
		element: Element,
		primaryKeyType: TypeName,
		primaryKey: String
	) =
		FunSpec.builder(RoomEntity<*, *>::primaryValue.name)
			.returns(primaryKeyType)
			.addModifiers(KModifier.OVERRIDE)
			.addStatement("return entity.${primaryKey} ")
			.addParameter("entity", element.asType().asTypeName())
			.build()

	private fun resultFunction(element: Element, columnNames: List<String>) =
		FunSpec.builder(RoomEntity<*, *>::result.name)
			.addModifiers(KModifier.OVERRIDE)
			.returns(element.asType().asTypeName())
			.addStatement(
				"return %T(%L)",
				element.asType().asTypeName(), columnNames.joinToString(",") { "rs.get(\"$it\")" }
			)
			.addParameter("rs", ResultSet::class)
			.build()
}
