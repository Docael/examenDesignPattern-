package sn.ism.payment.payment.mapper;

import org.springframework.stereotype.Component;
import sn.ism.payment.payment.dto.FactureRequest;
import sn.ism.payment.payment.dto.FactureResponse;
import sn.ism.payment.payment.model.Facture;

import java.time.LocalDate;

@Component
public class FactureMapper {

    public Facture toEntity(FactureRequest request) {
        LocalDate now = LocalDate.now();
        return Facture.builder()
                .reference(request.getReference())
                .walletCode(request.getWalletCode())
                .serviceName(request.getServiceName())
                .amount(request.getAmount())
                .month(request.getMonth() != null ? request.getMonth() : now.getMonthValue())
                .year(request.getYear() != null ? request.getYear() : now.getYear())
                .build();
    }

    public FactureResponse toResponse(Facture facture) {
        return FactureResponse.builder()
                .id(facture.getId())
                .reference(facture.getReference())
                .walletCode(facture.getWalletCode())
                .serviceName(facture.getServiceName())
                .amount(facture.getAmount())
                .month(facture.getMonth())
                .year(facture.getYear())
                .status(facture.getStatus())
                .createdAt(facture.getCreatedAt())
                .build();
    }
}
