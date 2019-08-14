package be.blockchaindeveloper.fabric_client.util;

/**
 *
 * @author jellediet
 */
import be.blockchaindeveloper.fabric_client.config.BlockchainNetworkAttributes;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
    private final long waitTime = 6000; //Milliseconds

    @Autowired
    @Qualifier("channel1")
    Channel channel;

    @Autowired
    HFClient hfClient;

    @Autowired
    ObjectMapper objectMapper;

    public String executeTransaction(boolean invoke, String func, String... args) throws InvalidArgumentException, ProposalException, UnsupportedEncodingException, InterruptedException, ExecutionException, TimeoutException {

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
                if (future.isDone()) {
                    TransactionEvent transactionEvent = future.get();
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: txid: " + transactionEvent.getTransactionID());
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
                    return null;
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
            result = executeTransaction(true, "set", args);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String getObjectByKey(String key) {
        String result = "";
        try {
            result = executeTransaction(false, "get", key);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String deleteObject(String key) {
        String result = "";
        try {
            result = executeTransaction(true, "delete", key);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String query(RichQuery query) {
        String result = "";
        try {
            String[] args = {objectMapper.writeValueAsString(query)};
            result = executeTransaction(false, "query", args);
        } catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException | JsonProcessingException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
