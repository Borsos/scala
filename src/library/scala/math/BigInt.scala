/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2006-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$


package scala.math

import java.math.BigInteger

/**
 *  @author  Martin Odersky
 *  @version 1.0, 15/07/2003
 *  @since 2.1
 */
object BigInt {

  private val minCached = -1024
  private val maxCached = 1024
  private val cache = new Array[BigInt](maxCached - minCached + 1)

  val MinLong = BigInt(Long.MinValue)
  val MaxLong = BigInt(Long.MaxValue)

  /** Constructs a <code>BigInt</code> whose value is equal to that of the
   *  specified integer value.
   *
   *  @param i the specified integer value
   *  @return  the constructed <code>BigInt</code>
   */
  def apply(i: Int): BigInt =
    if (minCached <= i && i <= maxCached) {
      val offset = i - minCached
      var n = cache(offset)
      if (n eq null) { n = new BigInt(BigInteger.valueOf(i)); cache(offset) = n }
      n
    } else new BigInt(BigInteger.valueOf(i))

  /** Constructs a <code>BigInt</code> whose value is equal to that of the
   *  specified long value.
   *
   *  @param l the specified long value
   *  @return  the constructed <code>BigInt</code>
   */
  def apply(l: Long): BigInt =
    if (minCached <= l && l <= maxCached) apply(l.toInt)
    else new BigInt(BigInteger.valueOf(l))

  /** Translates a byte array containing the two's-complement binary
   *  representation of a BigInt into a BigInt.
   */
  def apply(x: Array[Byte]): BigInt =
    new BigInt(new BigInteger(x))

  /** Translates the sign-magnitude representation of a BigInt into a BigInt.
   */
  def apply(signum: Int, magnitude: Array[Byte]): BigInt =
    new BigInt(new BigInteger(signum, magnitude))

  /** Constructs a randomly generated positive BigInt that is probably prime,
   *  with the specified bitLength.
   */
  def apply(bitlength: Int, certainty: Int, rnd: scala.util.Random): BigInt =
    new BigInt(new BigInteger(bitlength, certainty, rnd.self))

  /** Constructs a randomly generated BigInt, uniformly distributed over the
   *  range 0 to (2 ^ numBits - 1), inclusive.
   *
   *  @param numbits ...
   *  @param rnd     ...
   *  @return        ...
   */
  def apply(numbits: Int, rnd: scala.util.Random): BigInt =
    new BigInt(new BigInteger(numbits, rnd.self))

  /** Translates the decimal String representation of a BigInt into a BigInt.
   */
  def apply(x: String): BigInt =
    new BigInt(new BigInteger(x))

  /** Translates the string representation of a BigInt in the
   *  specified <code>radix</code> into a BigInt.
   *
   *  @param x     ...
   *  @param radix ...
   *  @return      ...
   */
  def apply(x: String, radix: Int): BigInt =
    new BigInt(new BigInteger(x, radix))

  /** Returns a positive BigInt that is probably prime, with the specified bitLength.
   */
  def probablePrime(bitLength: Int, rnd: scala.util.Random): BigInt =
    new BigInt(BigInteger.probablePrime(bitLength, rnd.self))

  /** Implicit conversion from <code>int</code> to <code>BigInt</code>.
   */
  implicit def int2bigInt(i: Int): BigInt = apply(i)

  /** Implicit conversion from long to BigInt
   */
  implicit def long2bigInt(l: Long): BigInt = apply(l)
}

/**
 *  @author  Martin Odersky
 *  @version 1.0, 15/07/2003
 */
@serializable
class BigInt(val bigInteger: BigInteger) extends ScalaNumber with ScalaNumericConversions
{
  /** Returns the hash code for this BigInt. */
  override def hashCode(): Int =
    if (this >= BigInt.MinLong && this <= BigInt.MaxLong) unifiedPrimitiveHashcode
    else bigInteger.hashCode

  /** Compares this BigInt with the specified value for equality.
   */
  override def equals(that: Any): Boolean = that match {
    case that: BigInt     => this equals that
    case that: BigDecimal => that.toBigIntExact exists (this equals _)
    case x                => (this <= BigInt.MaxLong && this >= BigInt.MinLong) && unifiedPrimitiveEquals(x)
  }

  protected[math] def isWhole = true
  def underlying = bigInteger

  /** Compares this BigInt with the specified BigInt for equality.
   */
  def equals (that: BigInt): Boolean = compare(that) == 0

  /** Compares this BigInt with the specified BigInt
   */
  def compare (that: BigInt): Int = this.bigInteger.compareTo(that.bigInteger)

  /** Less-than-or-equals comparison of BigInts
   */
  def <= (that: BigInt): Boolean = compare(that) <= 0

  /** Greater-than-or-equals comparison of BigInts
   */
  def >= (that: BigInt): Boolean = compare(that) >= 0

  /** Less-than of BigInts
   */
  def <  (that: BigInt): Boolean = compare(that) <  0

  /** Greater-than comparison of BigInts
   */
  def >  (that: BigInt): Boolean = compare(that) > 0

  /** Addition of BigInts
   */
  def +  (that: BigInt): BigInt = new BigInt(this.bigInteger.add(that.bigInteger))

  /** Subtraction of BigInts
   */
  def -  (that: BigInt): BigInt = new BigInt(this.bigInteger.subtract(that.bigInteger))

  /** Multiplication of BigInts
   */
  def *  (that: BigInt): BigInt = new BigInt(this.bigInteger.multiply(that.bigInteger))

  /** Division of BigInts
   */
  def /  (that: BigInt): BigInt = new BigInt(this.bigInteger.divide(that.bigInteger))

  /** Remainder of BigInts
   */
  def %  (that: BigInt): BigInt = new BigInt(this.bigInteger.remainder(that.bigInteger))

  /** Returns a pair of two BigInts containing (this / that) and (this % that).
   */
  def /% (that: BigInt): (BigInt, BigInt) = {
    val dr = this.bigInteger.divideAndRemainder(that.bigInteger)
    (new BigInt(dr(0)), new BigInt(dr(1)))
  }

  /** Leftshift of BigInt
   */
  def << (n: Int): BigInt = new BigInt(this.bigInteger.shiftLeft(n))

  /** (Signed) rightshift of BigInt
   */
  def >> (n: Int): BigInt = new BigInt(this.bigInteger.shiftRight(n))

  /** Bitwise and of BigInts
   */
  def &  (that: BigInt): BigInt = new BigInt(this.bigInteger.and(that.bigInteger))

  /** Bitwise or of BigInts
   */
  def |  (that: BigInt): BigInt = new BigInt(this.bigInteger.or (that.bigInteger))

  /** Bitwise exclusive-or of BigInts
   */
  def ^  (that: BigInt): BigInt = new BigInt(this.bigInteger.xor(that.bigInteger))

  /** Bitwise and-not of BigInts. Returns a BigInt whose value is (this & ~that).
   */
  def &~ (that: BigInt): BigInt = new BigInt(this.bigInteger.andNot(that.bigInteger))

  /** Returns the greatest common divisor of abs(this) and abs(that)
   */
  def gcd (that: BigInt): BigInt = new BigInt(this.bigInteger.gcd(that.bigInteger))

  /** Returns a BigInt whose value is (this mod m).
   *  This method differs from `%' in that it always returns a non-negative BigInt.
   */
  def mod (that: BigInt): BigInt = new BigInt(this.bigInteger.mod(that.bigInteger))

  /** Returns the minimum of this and that
   */
  def min (that: BigInt): BigInt = new BigInt(this.bigInteger.min(that.bigInteger))

  /** Returns the maximum of this and that
   */
  def max (that: BigInt): BigInt = new BigInt(this.bigInteger.max(that.bigInteger))

  /** Returns a BigInt whose value is (<tt>this</tt> raised to the power of <tt>exp</tt>).
   */
  def pow (exp: Int): BigInt = new BigInt(this.bigInteger.pow(exp))

  /** Returns a BigInt whose value is
   *  (<tt>this</tt> raised to the power of <tt>exp</tt> modulo <tt>m</tt>).
   */
  def modPow (exp: BigInt, m: BigInt): BigInt =
    new BigInt(this.bigInteger.modPow(exp.bigInteger, m.bigInteger))

  /** Returns a BigInt whose value is (the inverse of <tt>this</tt> modulo <tt>m</tt>).
   */
  def modInverse (m: BigInt): BigInt = new BigInt(this.bigInteger.modInverse(m.bigInteger))

  /** Returns a BigInt whose value is the negation of this BigInt
   */
  def unary_- : BigInt   = new BigInt(this.bigInteger.negate())

  /** Returns the absolute value of this BigInt
   */
  def abs: BigInt = new BigInt(this.bigInteger.abs())

  /** Returns the sign of this BigInt, i.e.
   *   -1 if it is less than 0,
   *   +1 if it is greater than 0
   *   0  if it is equal to 0
   */
  def signum: Int = this.bigInteger.signum()

  /** Returns the bitwise complement of this BigNum
   */
  def ~ : BigInt   = new BigInt(this.bigInteger.not())

  /** Returns true if and only if the designated bit is set.
   */
  def testBit (n: Int): Boolean = this.bigInteger.testBit(n)

  /** Returns a BigInt whose value is equivalent to this BigInt with the designated bit set.
   */
  def setBit  (n: Int): BigInt  = new BigInt(this.bigInteger.setBit(n))

  /** Returns a BigInt whose value is equivalent to this BigInt with the designated bit cleared.
   */
  def clearBit(n: Int): BigInt  = new BigInt(this.bigInteger.clearBit(n))

  /** Returns a BigInt whose value is equivalent to this BigInt with the designated bit flipped.
   */
  def flipBit (n: Int): BigInt  = new BigInt(this.bigInteger.flipBit(n))

  /** Returns the index of the rightmost (lowest-order) one bit in this BigInt
   * (the number of zero bits to the right of the rightmost one bit).
   */
  def lowestSetBit: Int         = this.bigInteger.getLowestSetBit()

  /** Returns the number of bits in the minimal two's-complement representation of this BigInt,
   *  excluding a sign bit.
   */
  def bitLength: Int            = this.bigInteger.bitLength()

  /** Returns the number of bits in the two's complement representation of this BigInt
   *  that differ from its sign bit.
   */
  def bitCount: Int             = this.bigInteger.bitCount()

  /** Returns true if this BigInt is probably prime, false if it's definitely composite.
   *  @param certainty  a measure of the uncertainty that the caller is willing to tolerate:
   *                    if the call returns true the probability that this BigInt is prime
   *                    exceeds (1 - 1/2 ^ certainty).
   *                    The execution time of this method is proportional to the value of
   *                    this parameter.
   */
  def isProbablePrime(certainty: Int) = this.bigInteger.isProbablePrime(certainty)

  /** Converts this BigInt to a <tt>byte</tt>.
   *  If the BigInt is too big to fit in a byte, only the low-order 8 bits are returned.
   *  Note that this conversion can lose information about the overall magnitude of the
   *  BigInt value as well as return a result with the opposite sign.
   */
  override def byteValue   = intValue.toByte

  /** Converts this BigInt to a <tt>short</tt>.
   *  If the BigInt is too big to fit in a byte, only the low-order 16 bits are returned.
   *  Note that this conversion can lose information about the overall magnitude of the
   *  BigInt value as well as return a result with the opposite sign.
   */
  override def shortValue  = intValue.toShort

  /** Converts this BigInt to a <tt>char</tt>.
   *  If the BigInt is too big to fit in a char, only the low-order 16 bits are returned.
   *  Note that this conversion can lose information about the overall magnitude of the
   *  BigInt value and that it always returns a positive result.
   */
  def charValue   = intValue.toChar

  /** Converts this BigInt to an <tt>int</tt>.
   *  If the BigInt is too big to fit in a char, only the low-order 32 bits
   *  are returned. Note that this conversion can lose information about the
   *  overall magnitude of the BigInt value as well as return a result with
   *  the opposite sign.
   */
  def intValue    = this.bigInteger.intValue

  /** Converts this BigInt to a <tt>long</tt>.
   *  If the BigInt is too big to fit in a char, only the low-order 64 bits
   *  are returned. Note that this conversion can lose information about the
   *  overall magnitude of the BigInt value as well as return a result with
   *  the opposite sign.
   */
  def longValue   = this.bigInteger.longValue

  /** Converts this BigInt to a <tt>float</tt>.
   *  if this BigInt has too great a magnitude to represent as a float,
   *  it will be converted to <code>Float.NEGATIVE_INFINITY</code> or
   *  <code>Float.POSITIVE_INFINITY</code> as appropriate.
   */
  def floatValue  = this.bigInteger.floatValue

  /** Converts this BigInt to a <tt>double</tt>.
   *  if this BigInt has too great a magnitude to represent as a float,
   *  it will be converted to <code>Float.NEGATIVE_INFINITY</code> or
   *  <code>Float.POSITIVE_INFINITY</code> as appropriate.
   */
  def doubleValue = this.bigInteger.doubleValue

  /** Create a NumericRange[BigInt] in range <code>[start;end)</code>
   *  with the specified step, where start is the target BigInt.
   *
   *  @param end    the end value of the range (exclusive)
   *  @param step   the distance between elements (defaults to 1)
   *  @return       the range
   */
  def until(end: BigInt, step: BigInt = BigInt(1)) = Range.BigInt(this, end, step)

  /** Like until, but inclusive of the end value.
   */
  def to(end: BigInt, step: BigInt = BigInt(1)) = Range.BigInt.inclusive(this, end, step)

  /** Returns the decimal String representation of this BigInt.
   */
  override def toString(): String = this.bigInteger.toString()

  /** Returns the String representation in the specified radix of this BigInt.
   */
  def toString(radix: Int): String = this.bigInteger.toString(radix)

  /** Returns a byte array containing the two's-complement representation of
   *  this BigInt. The byte array will be in big-endian byte-order: the most
   *  significant byte is in the zeroth element. The array will contain the
   *  minimum number of bytes required to represent this BigInt, including at
   *  least one sign bit.
   */
  def toByteArray: Array[Byte] = this.bigInteger.toByteArray()
}
