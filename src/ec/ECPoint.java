/**
 * This represents an elliptic curve point and its operation
 * @author Phone Le
 * @author Hung Vu
 * @author Duy Nguyen
 */
package ec;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public class ECPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The neutral element of the curve. */
    public static final ECPoint ZERO = new ECPoint(BigInteger.ZERO, BigInteger.ONE);
    /** The quantity d in the equation of E_521. */
    private static final BigInteger D = BigInteger.valueOf(-376014);
    /** Mersenne prime. */
    public static final BigInteger P = BigInteger.valueOf(2L).pow(521).subtract(BigInteger.ONE);
    /** x coordinate. */
    private BigInteger x;
    /** y coordinate. */
    private BigInteger y;
    /** The lengthy value used to compute R. */
    private static final String RSUB = "337554763258501705789107630418782636071904961214051226618635150085779108655765";
    /**
     * The number of points n on any Edwards curve is always a multiple of 4, and
     * for E_521 that number is n = 4r.
     */
    public static final BigInteger R = BigInteger.valueOf(2L).pow(519).subtract(new BigInteger(RSUB));
    /** The fixed length of the byte array form of this point. */
    public static final int BAL = P.toByteArray().length * 2;

    /**
     * Initializes a point on the E_521 curve with given x and y.
     * 
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public ECPoint(BigInteger x, BigInteger y) {
        if (!isValidPair(x, y))
            return;
        this.x = x;
        this.y = y;
    }

    /**
     * Create a curve point on the curve with the given x and a y that is generated
     * based on x with the formula y = sqrt( (1 - x^2) / ( 1 - d * x^2) ) mod p
     * 
     * @param x   the x coordinate of the curve point
     * @param lsb the least significant bit of the y coordinate
     */
    public ECPoint(BigInteger x, boolean lsb) {
        // Get point y of base point g, (+ or -)sqrt((1-x^2)/(1 + 376014x^2)) mod p
        // Where nominator is (1-x^2) and denominator is (1 + 376014x^2)
        BigInteger xSquare = x.pow(2);
        BigInteger radicandNominator = BigInteger.ONE.subtract(xSquare); // (1-x^2)
        BigInteger radicandDenominator = BigInteger.ONE.subtract(D.multiply(xSquare)); // (1 + 376014x^2)
        BigInteger sqrt = sqrt(radicandNominator.multiply(radicandDenominator.modInverse(P)), lsb); // Final y

        if (sqrt == null)
            throw new IllegalArgumentException("No square root of the provided x exists");
        this.x = x;
        this.y = sqrt.mod(P);
    }

    /**
     * Given any two points on (x1,y1) and (x2,y2) on the curve, their sum point is
     * demonstrated by the method below, this is called the Edwards point addition
     * formula.
     * 
     * @param other the other x and y point.
     * @return A new ECPoint that is the combination of (x1,y1) and (x2,y2).
     */
    public ECPoint add(ECPoint other) {
        BigInteger xNum = x.multiply(other.y).add(y.multiply(other.x));
        BigInteger yNum = y.multiply(other.y).subtract(x.multiply(other.x));
        BigInteger rightDem = D.multiply(x).multiply(other.x).multiply(y).multiply(other.y);
        BigInteger xDem = BigInteger.ONE.add(rightDem);
        BigInteger yDem = BigInteger.ONE.subtract(rightDem);
        BigInteger myX = xNum.multiply(xDem.modInverse(P)).mod(P);
        BigInteger myY = yNum.multiply(yDem.modInverse(P)).mod(P);
        return new ECPoint(myX, myY);
    }

    /**
     * Return X value of this curve point.
     */
    public BigInteger getX() {
        return x;
    }

    /**
     * Return Y value of this curve point.
     */
    public BigInteger getY() {
        return y;
    }

    /**
     * Negates the opposite of a curve point.
     * 
     * @return this point's opposite point
     */
    public ECPoint opposite() {
        return new ECPoint(x.negate().mod(P), y);
    }

    /**
     * 
     * @param s
     * @return
     */
    public ECPoint multiply(BigInteger s) {
        int k = s.bitLength();
        ECPoint V = ZERO;
        for (int i = k; i >= 0; i--) {
            V = V.add(V);
            if (s.testBit(i))
                V = V.add(this);
        }

        return V;
    }

    /**
     * Convert ECPoint into a byte array of a fixed size based on the value of P.
     * 
     * @return a byte array representation of this point.
     */
    public byte[] toByteArray() {
        byte[] result = new byte[BAL];
        // x, y's byte content
        byte[] xBytes = x.toByteArray(), yBytes = y.toByteArray();
        // x, y position in the result byte array
        int xPos = result.length / 2 - xBytes.length, yPos = result.length - yBytes.length;
        // If x or y is negative, apply appropriate sign extension
        if (x.signum() < 0)
            Arrays.fill(result, 0, xPos, (byte) 0xff);
        if (y.signum() < 0)
            Arrays.fill(result, result.length / 2, yPos, (byte) 0xff);
        // Put x and y's content onto result
        System.arraycopy(xBytes, 0, result, xPos, xBytes.length);
        System.arraycopy(yBytes, 0, result, yPos, yBytes.length);

        return result;
    }

    /**
     * Convert a byte array into an ECPoint.
     * 
     * @param pBytes the input byte array
     * @return the newly converted ECPoint.
     */
    public static ECPoint toECPoint(byte[] pBytes) {
        if (pBytes.length != BAL)
            throw new IllegalArgumentException("Provided byte array was not properly formatted");

        BigInteger x = new BigInteger(Arrays.copyOfRange(pBytes, 0, BAL / 2));
        BigInteger y = new BigInteger(Arrays.copyOfRange(pBytes, BAL / 2, BAL));

        return new ECPoint(x, y);
    }

    /**
     * Check if provided x and y coordinate pair are a point on the e5211 curve
     * where: x^2 + y^2 = 1 + d * (x^2) * y^2 where d = -376014
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a boolean indicating if the provided (x, y) is on E_521
     */
    private boolean isValidPair(BigInteger x, BigInteger y) {
        BigInteger l, r;
        if (x.equals(BigInteger.ZERO) && y.equals(BigInteger.ONE)) {
            r = BigInteger.ONE; // (1 + d * 0 * y^z) = 1
            l = BigInteger.ONE; // (0 + 1) = 1
        } else {
            l = x.pow(2).add(y.pow(2)).mod(P); // (x^2 + y^2) mod p
            r = BigInteger.ONE.add(D.multiply(x.pow(2).multiply(y.pow(2)))).mod(P); // (1 + d * x^2 * y^2) mod p
        }
        return l.equals(r);
    }

    /**
     * Compute a square root of v mod p with a specified least significant bit, if
     * such a root exists.
     *
     * @param v   the radicand.
     * @param p   the modulus (must satisfy p mod 4 = 3).
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true if such a
     *         root exists, otherwise null.
     */
    public static BigInteger sqrt(BigInteger v, boolean lsb) {
        assert (P.testBit(0) && P.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(P.shiftRight(2).add(BigInteger.ONE), P);
        if (r.testBit(0) != lsb) {
            r = P.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(P).signum() == 0) ? r : null;
    }

}
