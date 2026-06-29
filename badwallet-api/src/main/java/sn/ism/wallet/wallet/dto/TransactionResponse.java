package sn.ism.wallet.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sn.ism.wallet.common.enums.TransactionStatus;
import sn.ism.wallet.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionResponse {
    private String transactionId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal fees;
    private LocalDateTime date;
    private TransactionStatus status;
    private String description;
    private String senderPhone;
    private String receiverPhone;
}
