//import java.math.BigInteger;
//import java.security.spec.EllipticCurve;
//
//public class ECMult {
//  private EllipticCurve multiply (BigInteger s) {
//    // binary string of "s"
//    String binaryS = s.toString(2);
//
//    int k = binaryS.length();
//
//    // s = (s_k * s_k-1 … s_1 * s 0 ) 2 , s_k = 1.
//    EllipticCurve V = this;
//    for (int i = k - 1; i >= 0; i = i--){
//      V = V.add(V);
//      if (binaryS.charAt(i) == '1') V.add(P);
//    }
//
//    return V;
//  }
//}
