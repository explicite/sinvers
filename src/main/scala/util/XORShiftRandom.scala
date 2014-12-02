package util

import java.nio.ByteBuffer
import java.util.{ Random => JavaRandom }

import scala.util.hashing.MurmurHash3

class XORShiftRandom(init: Long) extends JavaRandom(init) {

  def this() = this(System.nanoTime)

  private var seed = XORShiftRandom.hashSeed(init)

  override protected def next(bits: Int): Int = {
    var nextSeed = seed ^ (seed << 21)
    nextSeed ^= (nextSeed >>> 35)
    nextSeed ^= (nextSeed << 4)
    seed = nextSeed
    (nextSeed & ((1L << bits) - 1)).asInstanceOf[Int]
  }

  def nextDouble(min: Double, max: Double): Double = ((nextDouble() * (max - min)) + 1d) + min - 1d

  override def setSeed(s: Long) {
    seed = XORShiftRandom.hashSeed(s)
  }

  def randomAlpha(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }

  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString()
  }
}

private object XORShiftRandom {
  private def hashSeed(seed: Long): Long = {
    val bytes = ByteBuffer.allocate(java.lang.Long.SIZE).putLong(seed).array()
    MurmurHash3.bytesHash(bytes)
  }

}
