package cn.tursom.socket.server;

import cn.tursom.core.buffer.ByteBuffer;
import cn.tursom.core.buffer.ByteBufferExtensionKt;
import cn.tursom.core.buffer.impl.DirectByteBuffer;
import cn.tursom.niothread.NioProtocol;
import cn.tursom.niothread.NioThread;
import cn.tursom.niothread.WorkerLoopNioThread;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NioLoopServerTest implements Runnable, Closeable {
    private static final NioProtocol protocol = new NioProtocol() {
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

        @Override
        public void exceptionCause(@NotNull SelectionKey key, @NotNull NioThread nioThread, @NotNull Throwable e) throws Throwable {
            e.printStackTrace();
            key.cancel();
            key.channel().close();
        }
    };
    private final int port;
    private final NioLoopServer server;

    public NioLoopServerTest(int port) {
        this.port = port;
        server = new NioLoopServer(port, protocol, 50, (threadName, workLoop) -> {
            try {
                return new WorkerLoopNioThread(threadName, Selector.open(), true, 3000, workLoop);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() {
        server.close();
    }

    @Override
    public void run() {
        server.run();
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        NioLoopServerTest server = new NioLoopServerTest(12345);
        server.run();
    }
}
