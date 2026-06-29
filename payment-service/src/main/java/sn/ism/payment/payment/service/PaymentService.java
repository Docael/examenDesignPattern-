package sn.ism.payment.payment.service;

import sn.ism.payment.payment.dto.FactureResponse;
import sn.ism.payment.payment.dto.PaidFactureResponse;
import sn.ism.payment.payment.dto.PayFactureRequest;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    PaidFactureResponse payCurrentFacture(PayFactureRequest request);

    PaidFactureResponse payFactures(PayFactureRequest request);

    List<FactureResponse> findCurrent(String walletCode);

    List<FactureResponse> findCurrentByService(String walletCode, String unite);

    List<FactureResponse> findBetweenDates(String walletCode, LocalDate start, LocalDate end);

    List<FactureResponse> findByReferences(List<String> references);

    void seedFactures();
}
