package sn.ism.wallet.common.utils;

import java.util.UUID;

/**
 * Genere un code unique de portefeuille (ex : WAL-AB12CD34).
 */
public final class CodeGenerator {

    private CodeGenerator() {
    }

    public static String walletCode() {
        String raw = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "WAL-" + raw;
    }

    public static String transactionReference() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
