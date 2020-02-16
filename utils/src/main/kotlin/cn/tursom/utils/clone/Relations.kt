package com.ddbes.kotlin.clone

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Relations(
    vararg val relations: Relation
)