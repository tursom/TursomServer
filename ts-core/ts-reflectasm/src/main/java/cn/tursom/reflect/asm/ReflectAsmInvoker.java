package cn.tursom.reflect.asm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectAsmInvoker {
    @Nullable
    public static Object invoke(MethodAccess methodAccess, Object obj, int index, Object[] args) {
        return methodAccess.invoke(obj, index, args);
    }

    @Nullable
    public static Object invoke(Method method, Object obj, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, args);
    }
}
