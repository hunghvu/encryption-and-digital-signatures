package ec;

import java.math.BigInteger;

public class ECSignature {
  private byte[] h;
  private BigInteger z;

  ECSignature(byte[] h, BigInteger z){
    this.h = h;
    this.z = z;
  }

  public byte[] get_h() {
    return this.h;
  }

  public BigInteger get_z(){
    return this.z;
  }
}
