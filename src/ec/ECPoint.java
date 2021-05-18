package ec;

import java.math.BigInteger;

public class ECPoint {

	/** The neutral element of the curve. */
    public static final ECPoint ZERO = new ECPoint(BigInteger.ZERO, BigInteger.ONE);
	/** The quantity d in the equation of E_521. */
    private static final BigInteger D = BigInteger.valueOf(-376014);
    /** x coordinate. */
    private BigInteger x;
    /** y coordinate. */
    private BigInteger y;
	/** Mersenne prime. */
	private static final BigInteger P = BigInteger.valueOf(2).pow(521).subtract(BigInteger.ONE);
	/** The lengthy value used to compute R. */
    private static final String RSUB = "337554763258501705789107630418782636071904961214051226618635150085779108655765";
    /**
     * The number of points n on any Edwards curve is always a multiple of 4, and for E_521
     * that number is n = 4r.
     */
    public static final BigInteger R = BigInteger.valueOf(2L).pow(519).subtract(new BigInteger(RSUB));

    
    /**
     * Initializes a point on the E_521 curve with given x and y.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public ECPoint(BigInteger x, BigInteger y) {
        if (!isValidPair(x, y)) throw new IllegalArgumentException("The x and y are not valid for a point on E_521");
        this.x = x;
        this.y = y;
    }


    /**
     * Given any two points on (x1,y1) and (x2,y2) on the curve, their sum point is demonstrated by the
     * method below, this is called the Edwards point addition formula.
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
        return new ECPoint(myX,myY);
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
    public ECPoint multiply (BigInteger s) {
        // binary string of "s"
        String binaryS = s.toString(2);

        int k = binaryS.length();

        // s = (s_k * s_k-1 … s_1 * s 0 ) 2 , s_k = 1.
        ECPoint V = this;
        for (int i = k - 1; i >= 0; i = i--){
          V = V.add(V);
          if (binaryS.charAt(i) == '1') V.add(this);
        }

        return V;
    }


    /**
     * Check if provided x and y coordinate pair are a point
     * on the e5211 curve where:
     * x^2 + y^2 = 1 + d * (x^2) * y^2 where d = -376014
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a boolean indicating if the provided (x, y)  is on E_521
     */
    private boolean isValidPair(BigInteger x, BigInteger y) {
        BigInteger l, r;

        l = x.pow(2).add(y.pow(2)).mod(P); // (x^2 + y^2) mod p
        r = BigInteger.ONE.add(D.multiply(x.pow(2).multiply(y.pow(2)))).mod(P); // (1 + d * x^2 * y^2) mod p

        return l.equals(r);
    }
    
    /**
    * Compute a square root of v mod p with a specified
    * least significant bit, if such a root exists.
    *
    * @param v the radicand.
    * @param p the modulus (must satisfy p mod 4 = 3).
    * @param lsb desired least significant bit (true: 1, false: 0).
    * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
    * if such a root exists, otherwise null.
    */
    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
    	assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
        return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
        r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }
    




}
