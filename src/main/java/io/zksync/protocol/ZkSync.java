package io.zksync.protocol;

import io.zksync.methods.request.Transaction;
import io.zksync.methods.response.*;
import org.jetbrains.annotations.Nullable;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendRawTransaction;

public interface ZkSync extends Web3j {
    static ZkSync build(Web3jService web3jService) {
        return new JsonRpc2_0ZkSync(web3jService);
    }

    /**
     * Estimate fee for the given transaction at the moment of the latest committed
     * block.
     * 
     * @param transaction Transaction data for estimation
     * @return Prepared estimate fee request
     */
    Request<?, ZksEstimateFee> zksEstimateFee(Transaction transaction);

    /**
     * Get address of main contract for current network chain.
     * 
     * @return Prepared main contract request
     */
    Request<?, ZksMainContract> zksMainContract();

    /**
     * Get list of the tokens supported by ZkSync.
     * 
     * @param from  Offset of tokens
     * @param limit Limit of amount of tokens to return
     * @return Prepared get confirmed tokens request
     */
    Request<?, ZksTokens> zksGetConfirmedTokens(Integer from, Short limit);

    /**
     * Get price of the token in USD.
     * 
     * @param tokenAddress Address of the token in hex format
     * @return Prepared get token price request
     */
    Request<?, ZksTokenPrice> zksGetTokenPrice(String tokenAddress);

    /**
     * Get chain identifier of the L1 chain.
     * 
     * @return Prepared l1 chainid request
     */
    Request<?, ZksL1ChainId> zksL1ChainId();

    Request<?, ZksContractDebugInfo> zksGetContractDebugInfo(String contractAddress);

    Request<?, ZksTransactionTrace> zksGetTransactionTrace(String transactionHash);

    Request<?, ZksAccountBalances> zksGetAllAccountBalances(String address);

    Request<?, ZksBridgeAddresses> zksGetBridgeContracts();

    Request<?, ZksMessageProof> zksGetL2ToL1MsgProof(Integer block, String sender, String message, @Nullable Long l2LogPosition);

    Request<?, EthEstimateGas> ethEstimateGas(Transaction transaction);

    Request<?, ZksTestnetPaymasterAddress> zksGetTestnetPaymaster();
}
