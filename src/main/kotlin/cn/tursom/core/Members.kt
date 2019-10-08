@file:Suppress("unused")

package cn.tursom.core

import java.lang.reflect.Member
import java.lang.reflect.Modifier


fun Member.isPublic(): Boolean {
	return Modifier.isPublic(this.modifiers)
}

fun Member.isPrivate(): Boolean {
	return Modifier.isPrivate(this.modifiers)
}

fun Member.isProtected(): Boolean {
	return Modifier.isProtected(this.modifiers)
}

fun Member.isStatic(): Boolean {
	return Modifier.isStatic(this.modifiers)
}

fun Member.isFinal(): Boolean {
	return Modifier.isFinal(this.modifiers)
}

fun Member.isSynchronized(): Boolean {
	return Modifier.isSynchronized(this.modifiers)
}

fun Member.isVolatile(): Boolean {
	return Modifier.isVolatile(this.modifiers)
}

fun Member.isTransient(): Boolean {
	return Modifier.isTransient(this.modifiers)
}

fun Member.isNative(): Boolean {
	return Modifier.isNative(this.modifiers)
}

fun Member.isInterface(): Boolean {
	return Modifier.isInterface(this.modifiers)
}

fun Member.isAbstract(): Boolean {
	return Modifier.isAbstract(this.modifiers)
}

fun Member.isStrict(): Boolean {
	return Modifier.isStrict(this.modifiers)
}
