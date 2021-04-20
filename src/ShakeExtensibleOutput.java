import java.util.Arrays;

public class ShakeExtensibleOutput {
    private byte[] bArr = new byte[200];  // initialize 8-bit bytes array, similar to *c from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.h
    private int pt, rsiz, mdlen;
    private boolean ext;

    public ShakeExtensibleOutput() {
        this.bArr = bArr;
        Arrays.fill(this.bArr,(byte)0);
        this.ext = false;

    }

    public void shake_xof(byte[] bArr) {
        this.bArr[this.pt] ^= (byte) (this.ext ? 0x04 : 0x1F);
        this.bArr[this.rsiz - 1] ^= (byte) 0x80;
        // sha3_keccakf(bArr); !!NEED TO CALL THIS METHOD
        this.pt = 0;
    }

    public void shake_out(byte[] bArr, byte[] out, int len) {
        int j = pt;
        for (int i = 0; i < len; i++) {
            if (j >= rsiz) {
                // sha3_keccakf(bArr); !!NEED TO CALL THIS METHOD
                j = 0;
            }
            out[i] = bArr[j++];
        }
        pt = j;
    }

}
