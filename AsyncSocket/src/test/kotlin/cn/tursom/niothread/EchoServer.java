package cn.tursom.niothread;

import cn.tursom.core.buffer.ByteBuffer;
import cn.tursom.core.buffer.ByteBufferExtensionKt;
import cn.tursom.core.buffer.impl.DirectByteBuffer;
import cn.tursom.niothread.loophandler.BossLoopHandler;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * AsyncSocket 的 NioThread 在 Java 下实现 Echo 服务器
 */
public class EchoServer implements Closeable, Runnable {
    public static void main(String[] args) {
        EchoServer server = new EchoServer(12345);
        server.run();
    }

    private final int port;
    private final ServerSocketChannel serverSocketChannel = getServerSocketChannel();
    private final NioThread nioThread = new WorkerLoopNioThread("nioLoopThread", getSelector(), false, 3000, loopHandler);
    private SelectionKey key;

    public EchoServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void close() throws IOException {
        if (key != null) {
            key.cancel();
        }
        serverSocketChannel.close();
        nioThread.close();
    }

    @Override
    public void run() {
        try {
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nioThread.register(serverSocketChannel, SelectionKey.OP_ACCEPT, key -> {
            this.key = key;
            return Unit.INSTANCE;
        });
    }

    private static final NioProtocol nioProtocol = new NioProtocol() {
        @Override
        public void exceptionCause(@NotNull SelectionKey key, @NotNull NioThread nioThread, @NotNull Throwable e) throws Throwable {
            e.printStackTrace();
            key.cancel();
            key.channel().close();
        }

        @Override
        public void handleConnect(@NotNull SelectionKey key, @NotNull NioThread nioThread) {
            key.interestOps(SelectionKey.OP_READ);
            key.attach(new DirectByteBuffer(1024));
        }

        @Override
        public void handleRead(@NotNull SelectionKey key, @NotNull NioThread nioThread) {
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBufferExtensionKt.read(channel, buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }

        @Override
        public void handleWrite(@NotNull SelectionKey key, @NotNull NioThread nioThread) {
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBufferExtensionKt.write(channel, buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    };

    private static final BossLoopHandler loopHandler = new BossLoopHandler(nioProtocol, null);

    private static Selector getSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ServerSocketChannel getServerSocketChannel() {
        try {
            return ServerSocketChannel.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
