package cn.tursom.web.utils

import cn.tursom.core.buffer.ByteBuffer

interface Chunked {
	/**
	 * Returns current transfer progress.
	 */
	val progress: Long

	/**
	 * Returns the length of the input.
	 * @return  the length of the input if the length of the input is known.
	 *          a negative value if the length of the input is unknown.
	 */
	val length: Long

	/**
	 * Return {@code true} if and only if there is no data left in the stream
	 * and the stream has reached at its end.
	 */
	val endOfInput: Boolean

	fun readChunk(): ByteBuffer

	/**
	 * Releases the resources associated with the input.
	 */
	fun close()
}