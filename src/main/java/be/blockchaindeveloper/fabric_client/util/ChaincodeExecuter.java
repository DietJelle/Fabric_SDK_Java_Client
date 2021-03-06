package be.blockchaindeveloper.fabric_client.util;

/**
 *
 * @author jellediet
 */
import be.blockchaindeveloper.fabric_client.config.BlockchainNetworkAttributes;
import be.blockchaindeveloper.fabric_client.model.Fish;
import be.blockchaindeveloper.fabric_client.model.FishPrivateData;
import be.blockchaindeveloper.fabric_client.model.TransactionHistory;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ChaincodeExecuter {

    private ChaincodeID ccId;
    private final long waitTime = 20000; //Milliseconds

    @Autowired
    @Qualifier("channel1")
    Channel channel;

    @Autowired
    HFClient hfClient;

    @Autowired
    ObjectMapper objectMapper;

    public String executeTransaction(boolean invoke, String func, String[] args, final String privateDataJson) throws InvalidArgumentException, ProposalException, UnsupportedEncodingException, InterruptedException, ExecutionException, TimeoutException {

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(BlockchainNetworkAttributes.CHAINCODE_1_NAME)
                .setVersion(BlockchainNetworkAttributes.CHAINCODE_1_VERSION);
        ccId = chaincodeIDBuilder.build();

        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setChaincodeLanguage(TransactionRequest.Type.JAVA);

        transactionProposalRequest.setFcn(func);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(waitTime);

        if (privateDataJson != null) {
            Map<String, byte[]> transientData = new HashMap<>();
            transientData.put("FishPrivateData", privateDataJson.getBytes("UTF-8"));
            transactionProposalRequest.setTransientMap(transientData);
        }
        String payload = "";

        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();

        // Java sdk will send transaction proposal to all peers, if some peer down but the response still meet the endorsement policy of chaincode,
        // there is no need to retry. If not, you should re-send the transaction proposal.
        Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, String.format("Sending transactionproposal to chaincode: function = " + func + " args = " + String.join(" , ", args)));
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {

            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                payload = new String(response.getChaincodeActionResponsePayload());
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, String.format("[√] Got success response from peer " + response.getPeer().getName() + " => Message : " + response.getMessage() + " Payload: %s ", payload));
                successful.add(response);
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, String.format("[×] Got failed response from peer " + response.getPeer().getName() + " => Message : " + msg + " Status :" + status));
                failed.add(response);
            }
        }

        if (invoke) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Sending transaction to orderers...");
            // Java sdk tries all orderers to send transaction, so don't worry about one orderer gone.
            try {
                CompletableFuture<TransactionEvent> future = channel.sendTransaction(successful);
                TransactionEvent transactionEvent = future.get();
                future.complete(transactionEvent);
                if (future.isDone()) {
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: txid: " + transactionEvent.getTransactionID());
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
                    return payload;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, "Orderer exception happened: " + ex);
                return null;
            }

        }
        return payload;
    }

    public String saveObject(String key, String json) {

        String result = "";
        String[] args = {key, json};
        try {
            result = executeTransaction(true, "set", args, null);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String saveFish(String key, Fish fish, FishPrivateData fishPrivateData) {

        String result = "";
        String[] args = {key, fish.toJSONString()};
        String privateData = null;
        if (fishPrivateData != null) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Saving private data:" + fishPrivateData.getOwner());
            privateData = fishPrivateData.toJSONString();
        }
        try {
            result = executeTransaction(true, "set", args, privateData);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String getObjectByKey(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransaction(false, "get", args, null);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String deleteObject(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransaction(true, "delete", args, null);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String query(RichQuery query) {
        String result = "";
        try {
            String[] args = {objectMapper.writeValueAsString(query)};
            result = executeTransaction(false, "query", args, null);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException | JsonProcessingException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<TransactionHistory> getHistory(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransaction(false, "getHistory", args, null);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<TransactionHistory> modifications = new ArrayList<>();
        TypeReference<List<TransactionHistory>> listType = new TypeReference<List<TransactionHistory>>() {
        };

        try {
            modifications = objectMapper.readValue(result, listType);
        } catch (IOException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modifications;
    }

}
