package sn.ism.wallet.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import sn.ism.wallet.common.enums.TransactionStatus;
import sn.ism.wallet.common.enums.TransactionType;
import sn.ism.wallet.common.utils.CodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal fees = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private LocalDateTime date;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiverWallet;

    @PrePersist
    public void prePersist() {
        if (date == null) date = LocalDateTime.now();
        if (reference == null) reference = CodeGenerator.transactionReference();
        if (fees == null) fees = BigDecimal.ZERO;
    }
}
