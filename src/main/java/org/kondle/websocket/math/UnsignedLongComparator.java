package org.kondle.websocket.math;

public class UnsignedLongComparator
{

    private static long mask = 0x7fffffffffffffffL;
    private static long r_mask = 0x8000000000000000L;

    /**
     * @return   is (u_long) a > (u_long) b
     */

    public static boolean u_isMore(long a, long b)
    {
        return (((a & r_mask) == (b & r_mask)) && (a & mask) > (b & mask)) ||
                (a & r_mask) == r_mask && ((b & r_mask) == 0L);
    }
    /**
     * @return   is (u_long) a < (u_long) b
     */
    public static boolean u_isLess(long a, long b)
    {
        return (((a & r_mask) == (b & r_mask)) && (a & mask) < (b & mask)) ||
                (a & r_mask) == 0L && ((b & r_mask) == r_mask);
    }
}
