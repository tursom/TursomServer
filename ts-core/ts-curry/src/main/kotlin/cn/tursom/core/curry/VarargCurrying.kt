@file:Suppress("unused")

package cn.tursom.core.curry

import cn.tursom.core.allMethods
import cn.tursom.core.plus
import cn.tursom.core.uncheckedCast


class VarargCurrying<A, R>(
    private val func: (Array<out A>) -> R,
    private val args: Array<out Any?>,
    private val componentType: Class<A>
) {
    companion object {
        inline operator fun <reified A, R> invoke(noinline func: (Array<out A>) -> R) =
            VarargCurrying(func, A::class.java)

        inline operator fun <reified A, R> invoke(noinline func: (Array<out A>) -> R, args: Array<out Any?>) =
            VarargCurrying(func, args, A::class.java)
    }

    constructor(
        func: (Array<out A>) -> R, componentType: Class<A> = func.javaClass.allMethods.find {
            it.name == "invoke" &&
              it.parameterTypes.size == 1 &&
              it.parameterTypes[0].isArray
        }!!.parameters[0].type.componentType.uncheckedCast()
    ) : this(func, emptyArray(), componentType)

    operator fun invoke(): R = if (componentType == Any::class.java) {
        func(args.uncheckedCast())
    } else {
        val componentArgs = java.lang.reflect.Array.newInstance(componentType, args.size)
        System.arraycopy(args, 0, componentArgs, 0, args.size)
        func(componentArgs.uncheckedCast())
    }

    operator fun invoke(vararg args: A) = VarargCurrying(func, this.args + args, componentType)
}

class BooleanVarargCurrying<R>(
    private val func: (BooleanArray) -> R,
    private val args: BooleanArray
) {
    constructor(func: (BooleanArray) -> R) : this(func, booleanArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Boolean) = BooleanVarargCurrying(func, this.args + args)
}

class CharVarargCurrying<R>(
    private val func: (CharArray) -> R,
    private val args: CharArray
) {
    constructor(func: (CharArray) -> R) : this(func, charArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Char) = CharVarargCurrying(func, this.args + args)
}

class ByteVarargCurrying<R>(
    private val func: (ByteArray) -> R,
    private val args: ByteArray
) {
    constructor(func: (ByteArray) -> R) : this(func, byteArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Byte) = ByteVarargCurrying(func, this.args + args)
}

class ShortVarargCurrying<R>(
    private val func: (ShortArray) -> R,
    private val args: ShortArray
) {
    constructor(func: (ShortArray) -> R) : this(func, shortArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Short) = ShortVarargCurrying(func, this.args + args)
}

class IntVarargCurrying<R>(
    private val func: (IntArray) -> R,
    private val args: IntArray
) {
    constructor(func: (IntArray) -> R) : this(func, intArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Int) = IntVarargCurrying(func, this.args + args)
}

class LongVarargCurrying<R>(
    private val func: (LongArray) -> R,
    private val args: LongArray
) {
    constructor(func: (LongArray) -> R) : this(func, longArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Long) = LongVarargCurrying(func, this.args + args)
}

class FloatVarargCurrying<R>(
    private val func: (FloatArray) -> R,
    private val args: FloatArray
) {
    constructor(func: (FloatArray) -> R) : this(func, floatArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Float) = FloatVarargCurrying(func, this.args + args)
}

class DoubleVarargCurrying<R>(
    private val func: (DoubleArray) -> R,
    private val args: DoubleArray
) {
    constructor(func: (DoubleArray) -> R) : this(func, doubleArrayOf())

    operator fun invoke() = func(args)
    operator fun invoke(vararg args: Double) = DoubleVarargCurrying(func, this.args + args)
}

inline fun <reified T, R> ((Array<out T>) -> R).currying() = VarargCurrying(this)
fun <R> ((BooleanArray) -> R).currying() = BooleanVarargCurrying(this)
fun <R> ((CharArray) -> R).currying() = CharVarargCurrying(this)
fun <R> ((ByteArray) -> R).currying() = ByteVarargCurrying(this)
fun <R> ((ShortArray) -> R).currying() = ShortVarargCurrying(this)
fun <R> ((IntArray) -> R).currying() = IntVarargCurrying(this)
fun <R> ((LongArray) -> R).currying() = LongVarargCurrying(this)
fun <R> ((FloatArray) -> R).currying() = FloatVarargCurrying(this)
fun <R> ((DoubleArray) -> R).currying() = DoubleVarargCurrying(this)

inline fun <reified T, R> ((Array<out T>) -> R).currying(vararg args: T) = VarargCurrying(this, args)
fun <R> ((BooleanArray) -> R).currying(vararg args: Boolean) = BooleanVarargCurrying(this, args)
fun <R> ((CharArray) -> R).currying(vararg args: Char) = CharVarargCurrying(this, args)
fun <R> ((ByteArray) -> R).currying(vararg args: Byte) = ByteVarargCurrying(this, args)
fun <R> ((ShortArray) -> R).currying(vararg args: Short) = ShortVarargCurrying(this, args)
fun <R> ((IntArray) -> R).currying(vararg args: Int) = IntVarargCurrying(this, args)
fun <R> ((LongArray) -> R).currying(vararg args: Long) = LongVarargCurrying(this, args)
fun <R> ((FloatArray) -> R).currying(vararg args: Float) = FloatVarargCurrying(this, args)
fun <R> ((DoubleArray) -> R).currying(vararg args: Double) = DoubleVarargCurrying(this, args)


