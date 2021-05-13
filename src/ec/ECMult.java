import java.math.BigInteger;
import java.security.spec.EllipticCurve;

public class ECMult {
  public EllipticCurve multiply (EllipticCurve P, BigInteger s) {
    // binary string of "s"
    String binaryS = s.toString(2);

    BigInteger k = BigInteger.valueOf(binaryS.length());

    // s = (s_k * s_k-1 … s_1 * s 0 ) 2 , s_k = 1.
    EllipticCurve V = P;
    for (BigInteger i = k.subtract(BigInteger.ONE); 
          (i.compareTo(BigInteger.ZERO) == 1 || i.compareTo(BigInteger.ZERO) == 0);
          i = i.subtract(BigInteger.ONE)){
      
      V = V.add(V);
      // Technically, I want to keep everything in BigInteger, however, a string size is limited to 2^31 - 1 in Java
      // So this step is strip down to only integer index access, which technically make the use of BigInteger index a waste.
      if (binaryS.charAt(i.intValue()) == '1') V.add(P);
    }

    return V;
  }
}
