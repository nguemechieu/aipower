package com.sopotek.aipower.component;

import org.springframework.stereotype.Component;
import org.stellar.sdk.*;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.operations.InvokeHostFunctionOperation;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.xdr.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class StellarSokobanSmartContract {
    static Server server = new Server("https://rpc-futurenet.stellar.org");
   static AccountResponse sourceAccount;



    public static void deploySmartContractXdr(String secretKey) throws IOException {

        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(secretKey);
      sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

      // Load the WASM file from a file
        byte[] wasmHash = Files.readAllBytes(Paths.get("smartcontract.wasm")); // Replace with your WASM file path



        // Define XDR for the host function
        HostFunction hostFunction = new HostFunction();
        hostFunction.setDiscriminant(HostFunctionType.HOST_FUNCTION_TYPE_CREATE_CONTRACT);

        ContractExecutable contractExecutable = new ContractExecutable();
        contractExecutable.setDiscriminant(ContractExecutableType.CONTRACT_EXECUTABLE_WASM);
        contractExecutable.setWasm_hash(Hash.fromXdrByteArray(wasmHash));

        // Create operation using raw XDR
        InvokeHostFunctionOperation operation =  InvokeHostFunctionOperation.builder()
               .hostFunction(hostFunction)
               .build();




        hostFunction.setDiscriminant(HostFunctionType.HOST_FUNCTION_TYPE_CREATE_CONTRACT);


        contractExecutable.setDiscriminant(ContractExecutableType.CONTRACT_EXECUTABLE_WASM);
        contractExecutable.setWasm_hash(Hash.fromXdrByteArray(wasmHash));


        // Build and submit the transaction
        Transaction transaction = new TransactionBuilder(sourceAccount, Network.FUTURENET)
                .addOperation(operation)
                .setTimeout(TransactionPreconditions.TIMEOUT_INFINITE)
                .setBaseFee(Transaction.MIN_BASE_FEE)
                .build();

        transaction.sign(sourceKeyPair);

        try {
            TransactionResponse response = server.submitTransaction(transaction);
            System.out.println("Transaction successful! Hash: " + response.getHash());
        } catch (Exception e) {
            System.err.println("Transaction failed: " + e.getMessage());
        }
}


}
