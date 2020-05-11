package com.sorrowblue.kdbc.compiler

import com.google.auto.service.AutoService
import com.sorrowblue.kdbc.Dao
import com.sorrowblue.kdbc.Database
import com.sorrowblue.kdbc.RoomDao
import com.sorrowblue.kdbc.RoomDatabase
import com.sorrowblue.kdbc.compiler.ktx.asClassName
import com.squareup.kotlinpoet.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic

@AutoService(Processor::class)
class DatabaseProcessor : AbstractProcessor() {
	companion object {
		const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
	}

	override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Database::class.java.name)

	override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

	override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
		roundEnv.getElementsAnnotatedWith(Database::class.java).forEach {
			if (it.kind != ElementKind.CLASS) {
				processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
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
			.superclass(element.asType().asTypeName())
		classBuilder.addProperty(
			PropertySpec.builder(
				RoomDatabase::connection.name,
				Connection::class,
				KModifier.OVERRIDE
			).initializer("%T.getConnection(url, user, password)", DriverManager::class.java).build()
		)
		element.enclosedElements.forEach {
			if (it.kind == ElementKind.METHOD && it.modifiers.contains(Modifier.ABSTRACT)) {
				it as ExecutableElement
				val functionBuilder = FunSpec.builder(it.simpleName.toString())
					.addModifiers(KModifier.OVERRIDE)
					.addStatement(
						"return %T(%L)", ClassName(
							it.returnType.asTypeName().asClassName().packageName + ".room",
							it.returnType.asTypeName().asClassName().simpleName + "_Imp"
						), RoomDatabase::connection.name
					)
					.returns(it.returnType.asTypeName())
				classBuilder.addFunction(functionBuilder.build())
			}
		}
		fileBuilder.addType(classBuilder.build()).build()
			.writeTo(File(processingEnv.options[DaoProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]!!))
	}

}
