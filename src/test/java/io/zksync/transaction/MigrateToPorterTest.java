package io.zksync.transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.utils.Numeric;

import io.zksync.abi.TransactionEncoder;
import io.zksync.crypto.eip712.Eip712Domain;
import io.zksync.crypto.eip712.Eip712Encoder;
import io.zksync.protocol.core.ZkSyncNetwork;

public class MigrateToPorterTest extends BaseTransactionTest {

    @Test
    public void testSerializeToEIP712() {
        MigrateToPorter migrateToPorter = buildMigrateToPorter();
        List<Pair<String, Type<?>>> types = migrateToPorter.eip712types();
        Iterator<Pair<String, Type<?>>> t = types.iterator();

        {
            Pair<String, Type<?>> t2 = t.next();
            assertEquals("accountAddress", t2.getKey());
            assertEquals(new Address(SENDER.getAddress()), t2.getValue());
        }
        super.assertSerializeToEIP712(t);
    }

    @Test
    public void testEncodeToEIP712TypeString() {
        MigrateToPorter migrateToPorter = buildMigrateToPorter();
        String result = Eip712Encoder.encodeType(migrateToPorter.intoEip712Struct());

        assertEquals(
                "MigrateToPorter(address accountAddress,address initiatorAddress,address feeToken,uint256 fee,uint32 nonce,uint64 validFrom,uint64 validUntil)",
                result);
    }

    @Test
    public void testSerializeToEIP712EncodedValue() {
        MigrateToPorter migrateToPorter = buildMigrateToPorter();
        byte[] encoded = Eip712Encoder.encodeValue(migrateToPorter.intoEip712Struct()).getValue();

        assertEquals("0xd627402011e9108c58eab136fc5da3ef8ff0d0a8c10a2c2fe41d7852bd23534c",
                Numeric.toHexString(encoded));
    }

    @Test
    public void testSerializeToEIP712Message() {
        MigrateToPorter migrateToPorter = buildMigrateToPorter();
        byte[] encoded = Eip712Encoder.typedDataToSignedBytes(Eip712Domain.defaultDomain(ZkSyncNetwork.Localhost),
                migrateToPorter);

        assertEquals("0xe06d27bdcbf687f5d41f059e3715a802850280401aed3c832b602b4b448fe588",
                Numeric.toHexString(encoded));
    }

    @Test
    public void testSerializeToEncodedFunction() {
        MigrateToPorter migrateToPorter = buildMigrateToPorter();
        Function function = TransactionEncoder.encodeToFunction(migrateToPorter);

        assertEquals(
                "0xdb828a440000000000000000000000007e5f4552091a69125d5dfcb7b8c2659029395bdf000000000000000000000000000000000000000000000000000000000000002a000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000ffffffff000000000000000000000000eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee000000000000000000000000000000000000000000000000000000000000007b0000000000000000000000007e5f4552091a69125d5dfcb7b8c2659029395bdf00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                FunctionEncoder.encode(function));
    }

    private MigrateToPorter buildMigrateToPorter() {
        MigrateToPorter zkMigrateToPorter = new MigrateToPorter(
                SENDER.getAddress(),
                SENDER.getAddress(),
                FEE_TOKEN.getAddress(),
                FEE,
                NONCE,
                VALIDITY_TIME);

        return zkMigrateToPorter;
    }
}
