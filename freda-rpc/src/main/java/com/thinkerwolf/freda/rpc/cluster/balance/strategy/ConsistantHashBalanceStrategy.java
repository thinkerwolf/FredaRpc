package com.thinkerwolf.freda.rpc.cluster.balance.strategy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.thinkerwolf.freda.common.Constants;
import com.thinkerwolf.freda.common.util.RandomUtil;
import com.thinkerwolf.freda.rpc.Invoker;
import com.thinkerwolf.freda.rpc.RequestMessage;
import com.thinkerwolf.freda.rpc.cluster.balance.BalanceStrategy;

/**
 * consistant hash balance strategy
 */
public class ConsistantHashBalanceStrategy implements BalanceStrategy {

    // private static final Logger logger =
    // LoggerFactory.getLogger(ConsistantHashBalanceStrategy.class);
    private Map<String, ConsistantHashSelector<?>> selectors = new ConcurrentHashMap<String, ConsistantHashSelector<?>>();

    private static String selectorKey(RequestMessage inv) {
        return inv.getMethodName() + "_" + Arrays.hashCode(inv.getParameterTypes());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers) {
        String key = selectorKey(inv);
        ConsistantHashSelector<?> selector = selectors.get(key);
        int indentityHashCode = System.identityHashCode(invokers);
        int nodeNum = inv.getParameter(Constants.NODE_NUM, Constants.DEFAULT_NODE_NUM);
        if (selector == null || selector.indentityHashCode != indentityHashCode) {
            selector = new ConsistantHashSelector<>(indentityHashCode, nodeNum, key, invokers);
            selectors.put(key, selector);
        }
        return (Invoker<T>) selector.select(inv);
    }

    private static final class ConsistantHashSelector<T> {
        private TreeMap<Long, Invoker<T>> vitualInvokerMap = new TreeMap<Long, Invoker<T>>();
        private int indentityHashCode;
        private int nodeNum;

        ConsistantHashSelector(int indentityHashCode, int nodeNum, String methodName, List<Invoker<T>> invokers) {
            this.indentityHashCode = indentityHashCode;
            this.nodeNum = nodeNum;

            for (Invoker<T> invoker : invokers) {
                for (int i = 0; i < nodeNum / 4; i++) {
                    byte[] digest = digest(methodName + i);
                    for (int h = 0; h < 4; h++) {
                        long hash = hash(digest, h);
                        vitualInvokerMap.put(hash, invoker);
                    }

                }
            }
        }

        private static long hash(byte[] digest, int num) {
//          crc32 use native, low efficiency
//			CRC32 crc = new CRC32();
//			crc.update(nodeName.getBytes());
//			return crc.getValue();
            int size = num * 4 % digest.length;
            size = size >= digest.length - 3 ? digest.length - 4 : size;
            return (((long) (digest[3 + size] & 0xFF) << 24)
                    | ((long) (digest[2 + size] & 0xFF) << 16)
                    | ((long) (digest[1 + size] & 0xFF) << 8)
                    | (digest[size] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        private static byte[] digest(String nodeName) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                return md.digest(nodeName.getBytes());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 algorithm not found", e);
            }
        }

        public Invoker<T> select(RequestMessage inv) {
            int i = RandomUtil.nextInt(nodeNum);
            Map.Entry<Long, Invoker<T>> entry = vitualInvokerMap.tailMap(hash(digest(inv.getMethodName()), i), true)
                    .firstEntry();
            if (entry == null) {
                entry = vitualInvokerMap.firstEntry();
            }
            return entry.getValue();
        }


    }

}
