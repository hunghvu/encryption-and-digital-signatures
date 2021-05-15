import java.math.BigInteger;

public class ECPoint {
	
	/** The quantity d in the equation of E_521. */
    private static final BigInteger D = BigInteger.valueOf(-376014);
    private static BigInteger x;
    private static BigInteger y;

    /** x coordinate. */
    //private BigInteger x;
    
    /** y coordinate. */
    //private BigInteger y;
    
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
    private static ECPoint summation(ECPoint other) {
        BigInteger xNum = x.multiply(other.y).add(y.multiply(other.x));
        BigInteger yNum = y.multiply(other.y).subtract(x.multiply(other.x));
        BigInteger rightDem = D.multiply(x).multiply(other.x).multiply(y).multiply(other.y);
        BigInteger xDem = BigInteger.ONE.add(rightDem);
        BigInteger yDem = BigInteger.ONE.subtract(rightDem);
        BigInteger myX = xNum.multiply(xDem.modInverse(P));
        BigInteger myY = yNum.multiply(yDem.modInverse(P));
        return new ECPoint(myX,myY);
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
    
    
	
	
}
