package sn.ism.payment.payment.model;

import jakarta.persistence.*;
import lombok.*;
import sn.ism.payment.common.enums.FactureStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String walletCode;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FactureStatus status = FactureStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = FactureStatus.PENDING;
    }
}
