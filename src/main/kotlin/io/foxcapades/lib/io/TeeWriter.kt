package io.foxcapades.lib.io

import java.io.IOException
import java.io.Writer

/**
 * # Tee Writer
 *
 * All writes and actions performed on this [TeeWriter] are performed on all of
 * the configured writers.
 *
 * If an exception is thrown by one or more of the underlying writers when
 * performing an action, the action will still be attempted on all other
 * underlying writers before the first exception is rethrown by this writer.
 *
 * **Example**
 * ```
 * var writer = TeeWriter(stdout, fileWriter1, fileWriter2, logWriter)
 *
 * reader.copyTo(writer)
 * ```
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 *
 * @constructor Constructs a new `TeeWriter` instance.
 *
 * @param writers Zero or more writers that all actions performed on this writer
 * will be applied to.
 */
class TeeWriter(vararg writers: Writer) : Writer() {
  private val writers = arrayOf(*writers)
  private var closed = false

  /**
   * Closes the underlying streams, flushing them first.
   *
   * Once the stream has been closed, further write() or flush() invocations
   * will cause an IOException to be thrown.
   *
   * Closing a previously closed stream has no effect.
   */
  override fun close() {
    if (closed)
      return

    closed = true

    var error: Throwable? = null

    writers.forEach {
      try {
        it.close()
      } catch (t: Throwable) {
        if (error == null)
          error = t
      }
    }

    if (error != null)
      throw error!!
  }

  /**
   * Flushes the underlying streams.
   *
   * If the streams have saved any characters from the various write() methods
   * in a buffer, write them immediately to their intended destinations.  Then,
   * if those destinations are other character or byte streams, flush them.
   * Thus one flush() invocation will flush all the buffers in a chain of
   * Writers and OutputStreams.
   *
   * If any of the intended destinations of this stream is an abstraction
   * provided by the underlying operating system, for example a file, then
   * flushing the stream guarantees only that bytes previously written to the
   * streams are passed to the operating system for writing; it does not
   * guarantee that they are actually written to a physical device such as a
   * disk drive.
   */
  override fun flush() {
    if (closed)
      throw IOException("Attempted to flush a TeeWriter after it was closed.")

    var error: Throwable? = null

    writers.forEach {
      try {
        it.flush()
      } catch (t: Throwable) {
        if (error == null)
          error = t
      }
    }

    if (error != null)
      throw error!!
  }

  /**
   * Writes a portion of an array of characters to the underlying writers.
   *
   * @param cbuf Array of characters
   *
   * @param off Offset from which to start writing characters
   *
   * @param len Number of characters to write
   */
  override fun write(cbuf: CharArray?, off: Int, len: Int) {
    if (closed)
      throw IOException("Attempted to write to a TeeWriter after it was closed.")

    var error: Throwable? = null

    writers.forEach {
      try {
        it.write(cbuf, off, len)
      } catch (t: Throwable) {
        if (error == null)
          error = t
      }

      if (error != null)
        throw error!!
    }
  }
}