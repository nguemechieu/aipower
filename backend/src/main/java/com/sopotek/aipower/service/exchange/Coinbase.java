package com.sopotek.aipower.service.exchange;//package com.sopotek.aipower.service.exchange;
//
//import java.io.StringReader;
//import java.security.PrivateKey;
//import java.security.Security;
//import java.security.KeyFactory;
//import java.security.interfaces.ECPrivateKey;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.time.Instant;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openssl.PEMParser;
//import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
//import org.bouncycastle.openssl.PEMKeyPair;
//
//import com.nimbusds.jose.*;
//import com.nimbusds.jose.crypto.ECDSASigner;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.SignedJWT;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//public class Coinbase {
//
//    private final String privateKeyPEM;
//    private final String keyId;
//
//    public Coinbase() throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//
//        Dotenv dotenv = Dotenv.load();
//        this.privateKeyPEM = dotenv.get("privateKey").replace("\\n", "\n");
//        this.keyId = dotenv.get("name");
//    }
//
//    private PrivateKey loadPrivateKey() throws Exception {
//        PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM));
//        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
//        Object object = pemParser.readObject();
//        pemParser.close();
//
//        if (object instanceof PEMKeyPair) {
//            return converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
//        } else {
//            throw new Exception("Unexpected private key format");
//        }
//    }
//
//    public String generateJWT(String requestMethod, String url) throws Exception {
//        Map<String, Object> header = new HashMap<>();
//        header.put("alg", "ES256");
//        header.put("typ", "JWT");
//        header.put("kid", keyId);
//        header.put("nonce", String.valueOf(Instant.now().getEpochSecond()));
//
//        String uri = requestMethod + " " + url;
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("iss", "cdp");
//        data.put("nbf", Instant.now().getEpochSecond());
//        data.put("exp", Instant.now().getEpochSecond() + 120);
//        data.put("sub", keyId);
//        data.put("uri", uri);
//
//        ECPrivateKey privateKey = (ECPrivateKey) loadPrivateKey();
//
//        // Create JWT claims
//        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
//        for (Map.Entry<String, Object> entry : data.entrySet()) {
//            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
//        }
//        JWTClaimsSet claimsSet = claimsSetBuilder.build();
//
//        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).customParams(header).build();
//        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
//
//        JWSSigner signer = new ECDSASigner( privateKey);
//        signedJWT.sign(signer);
//
//        return signedJWT.serialize();
//    }
//
//    public static void main(String[] args) {
//        try {
//            Coinbase coinbaseExchange = new Coinbase();
//
//            String requestMethod = "GET";
//            String url = "https://api.coinbase.com/api/v3/brokerage/accounts";
//
//            String jwt = coinbaseExchange.generateJWT(requestMethod, url);
//            System.out.println("Generated JWT: " + jwt);
//
//
//
//            // Set up headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + jwt);
//
//            // Create entity with headers
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            // Make request
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            System.out.println("Response: " + response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
