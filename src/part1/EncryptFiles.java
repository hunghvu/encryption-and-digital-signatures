//import java.awt.*;
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.SecureRandom;
//import java.util.Arrays;
//
//// Author: Paulo Barreto
//public class EncryptFiles {
//    static void encryptFile(FileDialog dialog, String passphrase) throws IOException {
//
//        String fileName = dialog.getDirectory() + dialog.getFile();
//        Path path = Paths.get(fileName);
//        byte[] message = Files.readAllBytes(path);
//        byte[] pw;
//
//        if (passphrase != null && passphrase.length() > 0) {
//            pw = passphrase.getBytes();
//        } else {
//            pw = new byte[0];
//        }
//
//        try {
//            FileOutputStream fos = new FileOutputStream(fileName + ".cript");
//            fos.write(encryptContents(message, pw));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Your file has been encrypted! Your file " + dialog.getFile() +
//                " encrypted to " + byteToHex(message));
//    }
//
//    // Help from stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal and Bob the Builder
//    private static String byteToHex(byte[] message) {
//        StringBuilder bobTheBuilder = new StringBuilder();
//        for (byte b: message) {
//            bobTheBuilder.append(String.format("%02X ", b));
//        }
//        return bobTheBuilder.toString();
//    }
//    private static byte[] encryptContents(byte[] message, byte[] pw) throws IOException {
//         // z <-- Random(512)
//        SecureRandom rand = new SecureRandom();
//        byte[] z = new byte[64]; //64 * 8 = 512
//        rand.nextBytes(z);
//
//
//        //(ke || ka) <-- KMACXOF256(z || pw, “”, 1024, “S”)
//        byte[] keka = SHAKE.KMACXOF256(concatenateByteArray(z, pw), "".getBytes(), 1024, "S".getBytes());
//
//        //c <-- KMACXOF256(ke, “”, |m|, “SKE”) XOR m
//        byte[] ke = Arrays.copyOfRange(keka, 0, 64);
//        byte[] intermediateResult = SHAKE.KMACXOF256(ke, "".getBytes(), 8 * message.length, "SKE".getBytes());
//        byte[] c = new byte[message.length];
//        for (int i = 0; i < message.length; i++) {
//            c[i] = (byte) (intermediateResult[i] ^ message[i]);
//        }
//
//        // t <-- KMACXOF256(ka, m, 512, “SKA”)
//        byte[] ka = Arrays.copyOfRange(keka, 64, 128);
//        byte[] t = SHAKE.KMACXOF256(ka, message, 512, "SKA".getBytes());
//
//        //symmetric cryptogram: (z, c, t)
//        ByteArrayOutputStream symCryptogram = new ByteArrayOutputStream();
//        symCryptogram.write(z);
//        symCryptogram.write(c);
//        symCryptogram.write(t);
//        return symCryptogram.toByteArray();
//    }
//
//}
