package sn.ism.wallet.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.ism.wallet.wallet.model.Transaction;
import sn.ism.wallet.wallet.model.Wallet;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet")
    List<Transaction> findByWallet(@Param("wallet") Wallet wallet);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet " +
            "ORDER BY t.date DESC")
    List<Transaction> findByWalletOrderByDateDesc(@Param("wallet") Wallet wallet);
}
