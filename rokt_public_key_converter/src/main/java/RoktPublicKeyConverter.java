import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class RoktPublicKeyConverter {

    private static final String PEM_EXTENSION = ".pem";

    private static final String MODULUS = "Modulus";
    private static final String EXPONENT = "Exponent";
    private static final String ALGORITHM = "RSA";
    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n";
    private static final String END_PUBLIC_KEY = "\n-----END PUBLIC KEY-----";

    public static void main(String[] args) throws Exception {
        String inputJsonFileName = args[0];
        String outputPemFileName = inputJsonFileName + PEM_EXTENSION;

        PublicKey publicKey = generateKeyFromInputFile(inputJsonFileName);
        writeKeyToOutputFile(publicKey, outputPemFileName);
    }

    private static PublicKey generateKeyFromInputFile(String jsonFileName) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFileName ));
        String modulusBase64 = (String) jsonObject.get(MODULUS);
        String exponentBase64 = (String) jsonObject.get(EXPONENT);

        byte[] modulusBytes = Base64.getDecoder().decode(modulusBase64.getBytes());
        byte[] exponentBytes = Base64.getDecoder().decode(exponentBase64.getBytes());

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    private static void writeKeyToOutputFile(PublicKey publicKey, String pemFileName) throws Exception {
        String keyString = Base64.getMimeEncoder().encodeToString(publicKey.getEncoded());
        String keyFileContent = BEGIN_PUBLIC_KEY + keyString + END_PUBLIC_KEY;
        try (PrintWriter printWriter = new PrintWriter(pemFileName)) {
            printWriter.println(keyFileContent);
        }
    }

}
