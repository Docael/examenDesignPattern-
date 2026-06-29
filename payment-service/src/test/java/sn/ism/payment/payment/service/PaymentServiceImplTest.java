package sn.ism.payment.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.ism.payment.common.enums.FactureStatus;
import sn.ism.payment.exception.BusinessException;
import sn.ism.payment.payment.dto.FactureResponse;
import sn.ism.payment.payment.dto.PaidFactureResponse;
import sn.ism.payment.payment.dto.PayFactureRequest;
import sn.ism.payment.payment.mapper.FactureMapper;
import sn.ism.payment.payment.model.Facture;
import sn.ism.payment.payment.repository.FactureRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private FactureRepository factureRepository;

    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl(factureRepository, new FactureMapper());
    }

    private Facture facture(String ref, String amount, FactureStatus status) {
        return Facture.builder()
                .reference(ref)
                .walletCode("WAL-DEMO0001")
                .serviceName("SENELEC")
                .amount(new BigDecimal(amount))
                .month(1)
                .year(2026)
                .status(status)
                .build();
    }

    @Test
    @DisplayName("payFactures : marque les factures PAID et renvoie le total")
    void payFactures_shouldMarkPaidAndReturnTotal() {
        Facture f1 = facture("FAC-1", "1000", FactureStatus.PENDING);
        Facture f2 = facture("FAC-2", "1500", FactureStatus.PENDING);
        when(factureRepository.findByReferenceIn(List.of("FAC-1", "FAC-2")))
                .thenReturn(List.of(f1, f2));
        when(factureRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        PayFactureRequest req = new PayFactureRequest();
        req.setWalletCode("WAL-DEMO0001");
        req.setReferences(List.of("FAC-1", "FAC-2"));

        PaidFactureResponse resp = service.payFactures(req);

        assertThat(resp.getTotalAmount()).isEqualByComparingTo("2500");
        assertThat(resp.getCount()).isEqualTo(2);
        assertThat(resp.getStatus()).isEqualTo("PAID");
        assertThat(f1.getStatus()).isEqualTo(FactureStatus.PAID);
        assertThat(f2.getStatus()).isEqualTo(FactureStatus.PAID);
    }

    @Test
    @DisplayName("payFactures : echoue si aucune facture ne correspond")
    void payFactures_shouldThrow_whenNoFactureFound() {
        when(factureRepository.findByReferenceIn(List.of("X"))).thenReturn(List.of());

        PayFactureRequest req = new PayFactureRequest();
        req.setWalletCode("WAL-DEMO0001");
        req.setReferences(List.of("X"));

        assertThrows(BusinessException.class, () -> service.payFactures(req));
    }

    @Test
    @DisplayName("findCurrent : renvoie les factures du mois courant")
    void findCurrent_shouldReturnList() {
        LocalDate now = LocalDate.now();
        Facture f = facture("FAC-1", "1000", FactureStatus.PENDING);
        f.setMonth(now.getMonthValue());
        f.setYear(now.getYear());
        when(factureRepository.findByWalletCodeAndMonthAndYear(
                "WAL-DEMO0001", now.getMonthValue(), now.getYear()))
                .thenReturn(List.of(f));

        List<FactureResponse> result = service.findCurrent("WAL-DEMO0001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReference()).isEqualTo("FAC-1");
    }
}
