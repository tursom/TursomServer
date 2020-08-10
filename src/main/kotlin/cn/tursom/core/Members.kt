@file:Suppress("unused")

package cn.tursom.core

import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Modifier

val fieldModifiers: Field = Field::class.java.getDeclaredField("modifiers").apply {
  isAccessible = true
}

var Field.public: Boolean
  get() = Modifier.isPublic(this.modifiers)
  set(value) {
    val modifier = Modifier.PUBLIC
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.private: Boolean
  get() = Modifier.isPrivate(this.modifiers)
  set(value) {
    val modifier = Modifier.PRIVATE
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.protected: Boolean
  get() = Modifier.isProtected(this.modifiers)
  set(value) {
    val modifier = Modifier.PROTECTED
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.static: Boolean
  get() = Modifier.isStatic(this.modifiers)
  set(value) {
    val modifier = Modifier.STATIC
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.final: Boolean
  get() = Modifier.isFinal(this.modifiers)
  set(value) {
    val modifier = Modifier.FINAL
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.synchronized: Boolean
  get() = Modifier.isSynchronized(this.modifiers)
  set(value) {
    val modifier = Modifier.SYNCHRONIZED
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.volatile: Boolean
  get() = Modifier.isVolatile(this.modifiers)
  set(value) {
    val modifier = Modifier.VOLATILE
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.transient: Boolean
  get() = Modifier.isTransient(this.modifiers)
  set(value) {
    val modifier = Modifier.TRANSIENT
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.native: Boolean
  get() = Modifier.isNative(this.modifiers)
  set(value) {
    val modifier = Modifier.NATIVE
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.`interface`: Boolean
  get() = Modifier.isInterface(this.modifiers)
  set(value) {
    val modifier = Modifier.INTERFACE
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.abstract: Boolean
  get() = Modifier.isAbstract(this.modifiers)
  set(value) {
    val modifier = Modifier.ABSTRACT
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

var Field.strict: Boolean
  get() = Modifier.isStrict(this.modifiers)
  set(value) {
    val modifier = Modifier.STRICT
    fieldModifiers.set(
      this,
      if (value) {
        modifiers or modifier
      } else {
        modifiers and modifier.inv()
      }
    )
  }

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
