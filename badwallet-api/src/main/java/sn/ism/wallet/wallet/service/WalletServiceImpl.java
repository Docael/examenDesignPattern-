package sn.ism.wallet.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ism.wallet.common.utils.CodeGenerator;
import sn.ism.wallet.exception.BusinessException;
import sn.ism.wallet.exception.ResourceNotFoundException;
import sn.ism.wallet.wallet.dto.BalanceResponse;
import sn.ism.wallet.wallet.dto.WalletRequest;
import sn.ism.wallet.wallet.dto.WalletResponse;
import sn.ism.wallet.wallet.mapper.WalletMapper;
import sn.ism.wallet.wallet.model.Wallet;
import sn.ism.wallet.wallet.repository.WalletRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public WalletResponse createWallet(WalletRequest request) {
        if (walletRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("Un portefeuille existe deja pour le numero " + request.getPhoneNumber());
        }
        if (walletRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Un portefeuille existe deja pour l'email " + request.getEmail());
        }

        Wallet wallet = walletMapper.toEntity(request);

        String code = (request.getCode() != null && !request.getCode().isBlank())
                ? request.getCode()
                : CodeGenerator.walletCode();
        if (walletRepository.findByCode(code).isPresent()) {
            throw new BusinessException("Ce code de portefeuille est deja utilise : " + code);
        }
        wallet.setCode(code);

        Wallet saved = walletRepository.save(wallet);
        return walletMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> findAll() {
        return walletRepository.findAll().stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse findByPhone(String phoneNumber) {
        Wallet wallet = getWalletOrThrow(phoneNumber);
        return walletMapper.toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String phoneNumber) {
        Wallet wallet = getWalletOrThrow(phoneNumber);
        return walletMapper.toBalanceResponse(wallet);
    }

    private Wallet getWalletOrThrow(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun portefeuille trouve pour le numero " + phoneNumber));
    }
}
