package io.zksync.helper;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Numeric;

import io.zksync.crypto.signer.EthSigner;
import io.zksync.protocol.ZkSync;
import io.zksync.transaction.ZkContract;
import io.zksync.transaction.fee.ZkTransactionFeeProvider;

public class CounterContract extends ZkContract {

    protected CounterContract(String contractBinary, String contractAddress, ZkSync zksync,
            TransactionManager transactionManager, ZkTransactionFeeProvider feeProvider, EthSigner signer) {
        super(contractBinary, contractAddress, zksync, transactionManager, feeProvider, signer);
    }

    protected CounterContract(String contractAddress, ZkSync zksync,
            TransactionManager transactionManager, ZkTransactionFeeProvider feeProvider, EthSigner signer) {
        super(contractAddress, zksync, transactionManager, feeProvider, signer);
    }

    public static final String FUNC_INCREMENT = "increment";
    public static final String FUNC_GET = "get";

    public static final String CODE = "DgAAAOAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAgCAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwACAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgAIACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAgAIAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4ACAAgAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADIAgAAF8fuka5Ruw7ZueQTMzv3T3AAAAAAAAAAAAAAAMgCAAALde4RcPa3w85Fg8ZdghmMQAAAAAAAAAAAAAAAUCAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgBAAAEHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaAIEAAgEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAQNABkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAECAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAEGQAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADABBAAGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AAQAgAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADABBAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AAQAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADABBAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUCAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAFlAGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAQQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABBABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAABB0AagAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAQfAGoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMgCAAAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAyAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoAAACACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMgCAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAyAIAAAAAAAAAAAAAAAAAAA/////wAAAAAAAAAAAAAADgCAAAEHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaiIAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AIAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGIAgAAAFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAQBAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwABAEAAAAAAAAAAAAAAAAAPOZMbQAAAAAAAAAAAAAABAgEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAABCwARgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAggAB/////////////////////wAAAAAAAAAAAAAADACCAAD///////////////////9/AAAAAAAAAAAAAAAEAgIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAIZwAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAEAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAQBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUQIAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoAAACAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAEAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAggABIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADACCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAQAYAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAABPAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAiAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAKPABlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAAkAAAFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAiIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4CAAIhBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgIIAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAQCAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABCAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAAAjkARAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AIABhAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACgAAAAE5ADkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAASAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAQQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAEAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAQBAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgABAGABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAgEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAACTwBOAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAiCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaAiQAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIQQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAACFQAUwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIgQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAIEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAABFcAVgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIIQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAACmcAWQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAQQAB/////////////////////wAAAAAAAAAAAAAADABBAAD/////////////////////AAAAAAAAAAAAAAAOACAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABoCAQEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADABBAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUEgAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAgEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAABmIAbAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKIIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABYQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAEAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AAADhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMgCAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAyAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAAA4QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADIAgAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMgCAAAAAAAAAAAAAAAAAAAHF7SE4AAAAAAAAAAAAAAA6AAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADIAgAAERAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMgCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA6AAAAgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADACCAAECAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAIIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AAgAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAAhAAAFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAABYQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgCJAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgIAASEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAgQAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAQQAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAAAnUAfQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4AAADhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABwAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    public static byte[] getCode() {
        return Base64.getDecoder().decode(CODE);
    }

    public static CounterContract load(String contractAddress, ZkSync zksync, TransactionManager transactionManager, ZkTransactionFeeProvider feeProvider, EthSigner signer) {
        return new CounterContract(contractAddress, zksync, transactionManager, feeProvider, signer);
    }

    public static RemoteCall<CounterContract> deploy(ZkSync web3j, Credentials credentials, ZkTransactionFeeProvider feeProvider) {
        return deployRemoteCall(CounterContract.class, web3j, credentials, feeProvider, Numeric.toHexString(getCode()), "");
    }

    public RemoteCall<TransactionReceipt> increment(BigInteger _value) {
        final Function function = encodeIncrement(_value);
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> get() {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static Function encodeIncrement(BigInteger _value) {
        return new Function(
            FUNC_INCREMENT, 
            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
            Collections.<TypeReference<?>>emptyList());
    }
    
}
