package ec;

import java.io.Serializable;
import java.math.BigInteger;

// This is stored in (*.sign) file.
public class ECSignature implements Serializable {
  private static final long serialVersionUID = 2L;
  private byte[] h;
  private BigInteger z;

  ECSignature(byte[] h, BigInteger z) {
    this.h = h;
    this.z = z;
  }

  public byte[] get_h() {
    return this.h;
  }

  public BigInteger get_z() {
    return this.z;
  }
}
