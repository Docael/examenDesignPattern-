package sn.ism.wallet.exception;

/**
 * Exception metier (solde insuffisant, doublon, montant invalide, etc.).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
