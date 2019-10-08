package cn.tursom.database

import java.lang.reflect.Field
import java.lang.reflect.Method

data class SqlFieldData(val field: Field, val getter: Method? = null)