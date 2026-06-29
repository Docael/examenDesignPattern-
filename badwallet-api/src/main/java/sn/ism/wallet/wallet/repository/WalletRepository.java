package sn.ism.wallet.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ism.wallet.wallet.model.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByPhoneNumber(String phoneNumber);

    Optional<Wallet> findByCode(String code);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
