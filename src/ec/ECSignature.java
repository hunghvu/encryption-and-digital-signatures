package ec;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

// This is stored in (*.sign) file.
public class ECSignature implements Serializable {
	
	/** The length of byte array that encapsulate h and z. */ 
    private static final int BAL = 130;
	
    private static final long serialVersionUID = 2L;
    private BigInteger h;
    private BigInteger z;

    public ECSignature(BigInteger h, BigInteger z) {
      this.h = h;
      this.z = z;
    }

    public BigInteger get_h() {
      return this.h;
    }

    public BigInteger get_z() {
      return this.z;
    }
  
    /**
     * Convert ECSignature into a byte array.
     * @return a byte array representation of this point.
     */
    public byte[] toByteArray() {
    	byte[] result = new byte[BAL];
    	// h, z's byte content
    	byte[] hBytes = h.toByteArray(), zBytes = z.toByteArray();
    	// h, z position in the result byte array
    	int hPos = result.length/2 - hBytes.length, zPos = result.length - zBytes.length;
    	//If x or y is negative, apply appropriate sign extension
    	if (h.signum() < 0) Arrays.fill(result, 0, hPos, (byte) 0xff);
    	if (z.signum() < 0) Arrays.fill(result, result.length/2, zPos, (byte) 0xff);
    	// Put h and z's content onto result
    	System.arraycopy(hBytes, 0, result, hPos, hBytes.length);
    	System.arraycopy(zBytes, 0, result, zPos, zBytes.length);
    	
    	return result;
    }
    
    /**
     * Convert a byte array into an ECSign.
     * @param pBytes the input byte array
     * @return the newly converted ECPoint.
     */
    public static ECSignature toECSignature(byte[] pBytes) {
    	if (pBytes.length != BAL) throw new IllegalArgumentException("Provided byte array was not properly formatted");
    	
    	BigInteger h = new BigInteger(Arrays.copyOfRange(pBytes, 0, BAL/2));
    	BigInteger z = new BigInteger(Arrays.copyOfRange(pBytes, BAL/2, BAL));
    	
    	return new ECSignature(h,z);
    }
    
}
