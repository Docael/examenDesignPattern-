package sn.ism.wallet.wallet.service;

import sn.ism.wallet.wallet.dto.BalanceResponse;
import sn.ism.wallet.wallet.dto.WalletRequest;
import sn.ism.wallet.wallet.dto.WalletResponse;

import java.util.List;

public interface WalletService {

    WalletResponse createWallet(WalletRequest request);

    List<WalletResponse> findAll();

    WalletResponse findByPhone(String phoneNumber);

    BalanceResponse getBalance(String phoneNumber);
}
