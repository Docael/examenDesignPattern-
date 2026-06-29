package sn.ism.wallet.wallet.mapper;

import org.springframework.stereotype.Component;
import sn.ism.wallet.wallet.dto.TransactionResponse;
import sn.ism.wallet.wallet.model.Transaction;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getReference())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .fees(transaction.getFees())
                .date(transaction.getDate())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .senderPhone(transaction.getSenderWallet() != null
                        ? transaction.getSenderWallet().getPhoneNumber() : null)
                .receiverPhone(transaction.getReceiverWallet() != null
                        ? transaction.getReceiverWallet().getPhoneNumber() : null)
                .build();
    }
}
