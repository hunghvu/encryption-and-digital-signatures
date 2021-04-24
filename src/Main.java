
import java.nio.charset.StandardCharsets;
import java.util.*;
public class Main {
  // We will decompose the project, and bring them into their respective folder later on (modularize).
  public static void main (String args[]) {
    if (test_sha3() == 0 && test_shake() == 0)
      System.out.println("FIPS 202 / SHA3, SHAKE128, SHAKE256 Self-Tests OK!\n");
  }

  public static int test_hexdigit(char ch) {
    if (ch >= '0' && ch <= '9')
      return ch - '0';
    if (ch >= 'A' && ch <= 'F')
      return ch - 'A' + 10;
    if (ch >= 'a' && ch <= 'f')
      return ch - 'a' + 10;
    return -1;
  }

public static int test_readhex(byte[] buf, final char[] str, int maxbytes)
{
    int i, h, l;

    for (i = 0; i < maxbytes; i++) {
        h = test_hexdigit(str[2 * i]);
        if (h < 0)
            return i;
        l = test_hexdigit(str[2 * i + 1]);
        if (l < 0)
            return i;
        buf[i] = (byte) ((h << 4) + l);
    }

    return i;
}

  // returns zero on success, nonzero + stderr messages on failure

public static int test_sha3()
{
    // message / digest pairs, lifted from ShortMsgKAT_SHA3-xxx.txt files
    // in the official package: https://github.com/gvanas/KeccakCodePackage

    // char* is effectively a string
    final String testvec[][] = {
        {   // SHA3-224, corner case with 0-length message
            "",
            "6B4E03423667DBB73B6E15454F0EB1ABD4597F9A1B078E3F5B5A6BC7"
        },
        {   // SHA3-256, short message
            "9F2FCC7C90DE090D6B87CD7E9718C1EA6CB21118FC2D5DE9F97E5DB6AC1E9C10",
            "2F1A5F7159E34EA19CDDC70EBF9B81F1A66DB40615D7EAD3CC1F1B954D82A3AF"
        },
        {   // SHA3-384, exact block size
            "E35780EB9799AD4C77535D4DDB683CF33EF367715327CF4C4A58ED9CBDCDD486"
            + "F669F80189D549A9364FA82A51A52654EC721BB3AAB95DCEB4A86A6AFA93826D"
            + "B923517E928F33E3FBA850D45660EF83B9876ACCAFA2A9987A254B137C6E140A"
            + "21691E1069413848",
            "D1C0FA85C8D183BEFF99AD9D752B263E286B477F79F0710B0103170173978133"
            + "44B99DAF3BB7B1BC5E8D722BAC85943A"
        },
        {   // SHA3-512, multiblock message
            "3A3A819C48EFDE2AD914FBF00E18AB6BC4F14513AB27D0C178A188B61431E7F5"
            + "623CB66B23346775D386B50E982C493ADBBFC54B9A3CD383382336A1A0B2150A"
            + "15358F336D03AE18F666C7573D55C4FD181C29E6CCFDE63EA35F0ADF5885CFC0"
            + "A3D84A2B2E4DD24496DB789E663170CEF74798AA1BBCD4574EA0BBA40489D764"
            + "B2F83AADC66B148B4A0CD95246C127D5871C4F11418690A5DDF01246A0C80A43"
            + "C70088B6183639DCFDA4125BD113A8F49EE23ED306FAAC576C3FB0C1E256671D"
            + "817FC2534A52F5B439F72E424DE376F4C565CCA82307DD9EF76DA5B7C4EB7E08"
            + "5172E328807C02D011FFBF33785378D79DC266F6A5BE6BB0E4A92ECEEBAEB1",
            "6E8B8BD195BDD560689AF2348BDC74AB7CD05ED8B9A57711E9BE71E9726FDA45"
            + "91FEE12205EDACAF82FFBBAF16DFF9E702A708862080166C2FF6BA379BC7FFC2"
        }
    };

    int i, fails, msg_len, sha_len;
    byte [] sha = new byte[64];
    byte [] buf = new byte[64];
    byte [] msg = new byte[256];
    Sha3 sha3 = new Sha3();

    fails = 0;
    for (i = 0; i < 4; i++) {

        // size = 1 byte * arr length
        msg_len = test_readhex(msg, testvec[i][0].toCharArray(), 256);
        sha_len = test_readhex(sha, testvec[i][1].toCharArray(), 64);

        sha3.Keccak(msg, msg_len, sha_len); // Where?

        if (!String.valueOf(sha).equals(String.valueOf(buf))) {
            System.out.println("[%" + i + "] SHA3-" + sha_len * 8 +", len " + msg_len + " test FAILED.\n");
            fails++;
        }
    }
    return fails;
}

  // test for SHAKE128 and SHAKE256

public static int test_shake()
{
    // Test vectors have bytes 480..511 of XOF output for given inputs.
    // From http://csrc.nist.gov/groups/ST/toolkit/examples.html#aHashing

    final String[] testhex = {
        // SHAKE128, message of length 0
        "43E41B45A653F2A5C4492C1ADD544512DDA2529833462B71A41A45BE97290B6F",
        // SHAKE256, message of length 0
        "AB0BAE316339894304E35877B0C28A9B1FD166C796B9CC258A064A8F57E27F2A",
        // SHAKE128, 1600-bit test pattern
        "44C9FB359FD56AC0A9A75A743CFF6862F17D7259AB075216C0699511643B6439",
        // SHAKE256, 1600-bit test pattern
        "6A1A9D7846436E4DCA5728B6F760EEF0CA92BF0BE5615E96959D767197A0BEEB"
    };

    int i, j, fails;
    // sha3_ctx_t sha3;
    Sha3 sha3 = new Sha3();
    byte[] buf = new byte[32]; 
    byte[] ref = new byte[32];

    fails = 0;

    for (i = 0; i < 4; i++) {

        if ((i & 1) == 0) {             // test each twice
            sha3.shake128_init();
        } else {
            sha3.shake256_init();
        }

        if (i >= 2) {                   // 1600-bit test pattern
            for (int k = 0; k < 20; k++) {
              buf[k] = (byte) 0xA3;
            }
            for (j = 0; j < 200; j += 20)
                // shake_update(&sha3, buf, 20); to sha3_update?
                sha3.sha3_update(new String(buf).getBytes(StandardCharsets.UTF_8), 20);
        }

        sha3.shake_xof();               // switch to extensible output

        for (j = 0; j < 512; j += 32)   // output. discard bytes 0..479
            sha3.shake_out(new String(buf).getBytes(StandardCharsets.UTF_8), 32);

        // compare to reference
        test_readhex(ref, testhex[i].toCharArray(), 32);
        if (!String.valueOf(buf).equals(String.valueOf(ref))){
          // bitwise &?
            System.out.println("[%" + i + "] SHAKE" + ((i & 1)!=0 ? 256 : 128) +", len %" + (i >= 2 ? 1600 : 0) +" test FAILED.\n");
            fails++;
        }
    }

    return fails;
}


}