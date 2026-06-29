package sn.ism.wallet.common.utils;

import sn.ism.wallet.common.enums.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcul simple des frais selon le type d'operation.
 *  - DEPOSIT  : 0%
 *  - PAYMENT  : 0%
 *  - WITHDRAW : 1%
 *  - TRANSFER : 1%
 */
public final class FeeCalculator {

    private static final BigDecimal ONE_PERCENT = new BigDecimal("0.01");

    private FeeCalculator() {
    }

    public static BigDecimal compute(TransactionType type, BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return switch (type) {
            case WITHDRAW, TRANSFER -> amount.multiply(ONE_PERCENT).setScale(2, RoundingMode.HALF_UP);
            case DEPOSIT, PAYMENT -> BigDecimal.ZERO;
        };
    }
}
