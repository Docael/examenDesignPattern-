package sn.ism.wallet.wallet.service;

import sn.ism.wallet.wallet.dto.*;

import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(String phoneNumber, DepositRequest request);

    TransactionResponse withdraw(WithdrawRequest request);

    TransactionResponse transfer(TransferRequest request);

    TransactionResponse pay(PaymentRequest request);

    TransactionResponse payFactures(PayFactureRequest request);

    List<TransactionResponse> history(String phoneNumber);

    List<WalletResponse> seed();
}

