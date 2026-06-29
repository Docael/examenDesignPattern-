package sn.ism.payment.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final FactureRepository factureRepository;
    private final FactureMapper factureMapper;

    // ---------------------------------------------------------------------
    // POST /pay  -> regle les factures du mois courant
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public PaidFactureResponse payCurrentFacture(PayFactureRequest request) {
        LocalDate now = LocalDate.now();
        List<Facture> factures = factureRepository
                .findByWalletCodeAndMonthAndYear(request.getWalletCode(), now.getMonthValue(), now.getYear())
                .stream()
                .filter(f -> f.getStatus() == FactureStatus.PENDING)
                .filter(f -> request.getServiceName() == null
                        || request.getServiceName().equalsIgnoreCase(f.getServiceName()))
                .toList();

        if (factures.isEmpty()) {
            throw new BusinessException("Aucune facture en attente pour le mois courant");
        }
        return markAsPaid(request.getWalletCode(), factures);
    }

    // ---------------------------------------------------------------------
    // POST /pay-factures  -> regle des factures par reference
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public PaidFactureResponse payFactures(PayFactureRequest request) {
        if (request.getReferences() == null || request.getReferences().isEmpty()) {
            return payCurrentFacture(request);
        }

        List<Facture> factures = factureRepository.findByReferenceIn(request.getReferences());
        if (factures.isEmpty()) {
            throw new BusinessException("Aucune facture trouvee pour les references fournies");
        }
        List<Facture> pending = factures.stream()
                .filter(f -> f.getStatus() == FactureStatus.PENDING)
                .toList();
        if (pending.isEmpty()) {
            throw new BusinessException("Toutes les factures fournies sont deja payees");
        }
        return markAsPaid(request.getWalletCode(), pending);
    }

    // ---------------------------------------------------------------------
    // GET /factures/current
    // ---------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> findCurrent(String walletCode) {
        LocalDate now = LocalDate.now();
        return factureRepository
                .findByWalletCodeAndMonthAndYear(walletCode, now.getMonthValue(), now.getYear())
                .stream().map(factureMapper::toResponse).toList();
    }

    // ---------------------------------------------------------------------
    // GET /factures/current?unite=...
    // ---------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> findCurrentByService(String walletCode, String unite) {
        LocalDate now = LocalDate.now();
        return factureRepository
                .findByWalletCodeAndServiceNameAndMonthAndYear(
                        walletCode, unite, now.getMonthValue(), now.getYear())
                .stream().map(factureMapper::toResponse).toList();
    }

    // ---------------------------------------------------------------------
    // GET /factures/periode
    // ---------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> findBetweenDates(String walletCode, LocalDate start, LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);
        return factureRepository
                .findByWalletCodeAndCreatedAtBetween(walletCode, startDt, endDt)
                .stream().map(factureMapper::toResponse).toList();
    }

    // ---------------------------------------------------------------------
    // GET /factures?references=...  (consomme par le wallet avant debit)
    // ---------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> findByReferences(List<String> references) {
        return factureRepository.findByReferenceIn(references)
                .stream().map(factureMapper::toResponse).toList();
    }

    // ---------------------------------------------------------------------
    // SEED
    // ---------------------------------------------------------------------
    @Override
    @Transactional
    public void seedFactures() {
        if (factureRepository.count() > 0) {
            return;
        }
        LocalDate now = LocalDate.now();
        int m = now.getMonthValue();
        int y = now.getYear();

        save("FAC-SEN-001", "WAL-DEMO0001", "SENELEC", new BigDecimal("15400"), m, y);
        save("FAC-SDE-001", "WAL-DEMO0001", "SDE", new BigDecimal("8200"), m, y);
        save("FAC-SON-001", "WAL-DEMO0001", "SONATEL", new BigDecimal("22000"), m, y);
        save("FAC-SEN-002", "WAL-DEMO0002", "SENELEC", new BigDecimal("31250"), m, y);
        save("FAC-SDE-002", "WAL-DEMO0002", "SDE", new BigDecimal("12750"), m, y);
        save("FAC-SEN-003", "WAL-DEMO0003", "SENELEC", new BigDecimal("9800"), m, y);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------
    private void save(String ref, String walletCode, String service, BigDecimal amount, int month, int year) {
        factureRepository.save(Facture.builder()
                .reference(ref)
                .walletCode(walletCode)
                .serviceName(service)
                .amount(amount)
                .month(month)
                .year(year)
                .status(FactureStatus.PENDING)
                .build());
    }

    private PaidFactureResponse markAsPaid(String walletCode, List<Facture> factures) {
        BigDecimal total = BigDecimal.ZERO;
        for (Facture f : factures) {
            f.setStatus(FactureStatus.PAID);
            total = total.add(f.getAmount());
        }
        factureRepository.saveAll(factures);

        return PaidFactureResponse.builder()
                .walletCode(walletCode)
                .references(factures.stream().map(Facture::getReference).toList())
                .totalAmount(total)
                .count(factures.size())
                .status("PAID")
                .build();
    }
}
