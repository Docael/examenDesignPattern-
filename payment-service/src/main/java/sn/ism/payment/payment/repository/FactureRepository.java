package sn.ism.payment.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ism.payment.payment.model.Facture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByReference(String reference);

    List<Facture> findByReferenceIn(List<String> references);

    List<Facture> findByWalletCode(String walletCode);

    List<Facture> findByWalletCodeAndMonthAndYear(String walletCode, Integer month, Integer year);

    List<Facture> findByWalletCodeAndServiceNameAndMonthAndYear(
            String walletCode, String serviceName, Integer month, Integer year);

    List<Facture> findByWalletCodeAndCreatedAtBetween(
            String walletCode, LocalDateTime start, LocalDateTime end);
}