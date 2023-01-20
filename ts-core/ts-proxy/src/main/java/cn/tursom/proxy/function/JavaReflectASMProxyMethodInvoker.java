package cn.tursom.proxy.function;

import cn.tursom.proxy.container.ProxyContainer;
import cn.tursom.proxy.container.ProxyMethodCacheFunction;
import com.esotericsoftware.reflectasm.MethodAccess;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class JavaReflectASMProxyMethodInvoker implements ProxyMethodCacheFunction {
    private final Object self;
    private final MethodAccess methodAccess;
    private final int index;

    public JavaReflectASMProxyMethodInvoker(Object self, MethodAccess methodAccess, int index) {
        this.self = self;
        this.methodAccess = methodAccess;
        this.index = index;
    }

    @Nullable
    @Override
    public Object invoke(
        @Nullable Object obj,
        @NotNull ProxyContainer c,
        @Nullable Method method,
        @Nullable Object[] args,
        @Nullable MethodProxy proxy
    ) {
        return methodAccess.invoke(self, index, args);
    }
}
