ts-delegation 是使用kotlin的 [属性委托](https://www.kotlincn.net/docs/reference/delegated-properties.html) 语法实现的一个属性委托库。其使用

其核心接口为 `DelegatedField` 与 `MutableDelegatedField`。这两个接口分别定义了实现属性委托的 getter 与 setter，具体方法分别如下表：

|           类           |                                 方法                                  |
|:---------------------:|:-------------------------------------------------------------------:|
|    DelegatedField     |    operator fun getValue(thisRef: T, property: KProperty<*>): V     |
| MutableDelegatedField | operator fun setValue(thisRef: T, property: KProperty<*>, value: V) |

`setValue` 的默认实现会先调用 `valueOnSet(thisRef, property, value, getValue())` 后 调用 `setValue(value)`
，这是由这两个函数的功能决定的。`fun setValue(value: V)` 则负责值的具体设置实现，而函数 `valueOnSet` 负责监视设置属性事件的实现。将 `valueOnSet`
独立出的原因是一些功能需要独立的方法实现，比如负责异步执行的实现`ExecutorMutableDelegatedField`就会将监听器的具体调用放到线程池里执行。