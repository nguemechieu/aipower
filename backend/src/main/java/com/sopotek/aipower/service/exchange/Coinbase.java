package com.sopotek.aipower.service.exchange;//package com.sopotek.aipower.service.exchange;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.PEMKeyPair;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Coinbase {

    private final String privateKeyPEM;
    private final String keyId;

    public Coinbase() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        Dotenv dotenv = Dotenv.load();
        this.privateKeyPEM = dotenv.get("privateKey").replace("\\n", "\n");
        this.keyId = dotenv.get("name");
    }

    private PrivateKey loadPrivateKey() throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        pemParser.close();

        if (object instanceof PEMKeyPair) {
            return converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
        } else {
            throw new Exception("Unexpected private key format");
        }
    }

    public String generateJWT(String requestMethod, String url) throws Exception {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "ES256");
        header.put("typ", "JWT");
        header.put("kid", keyId);
        header.put("nonce", String.valueOf(Instant.now().getEpochSecond()));

        String uri = requestMethod + " " + url;

        Map<String, Object> data = new HashMap<>();
        data.put("iss", "cdp");
        data.put("nbf", Instant.now().getEpochSecond());
        data.put("exp", Instant.now().getEpochSecond() + 120);
        data.put("sub", keyId);
        data.put("uri", uri);

        ECPrivateKey privateKey = (ECPrivateKey) loadPrivateKey();

        // Create JWT claims
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }
        JWTClaimsSet claimsSet = claimsSetBuilder.build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).customParams(header).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

        JWSSigner signer = new ECDSASigner( privateKey);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public ResponseEntity<String> sendRequest(String jwt, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }
}
