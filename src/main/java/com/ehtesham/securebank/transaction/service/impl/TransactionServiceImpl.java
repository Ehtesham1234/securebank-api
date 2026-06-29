package com.ehtesham.securebank.transaction.service.impl;

import com.ehtesham.securebank.account.entity.Account;
import com.ehtesham.securebank.account.repository.AccountRepository;
import com.ehtesham.securebank.account.service.AccountService;
import com.ehtesham.securebank.common.enums.AccountStatus;
import com.ehtesham.securebank.common.enums.TransactionStatus;
import com.ehtesham.securebank.common.enums.TransactionType;
import com.ehtesham.securebank.common.exception.AccountOperationException;
import com.ehtesham.securebank.common.exception.ResourceNotFoundException;
import com.ehtesham.securebank.transaction.dto.*;
import com.ehtesham.securebank.transaction.entity.Transaction;
import com.ehtesham.securebank.transaction.repository.TransactionRepository;
import com.ehtesham.securebank.transaction.service.TransactionService;
import com.ehtesham.securebank.user.entity.User;
import com.ehtesham.securebank.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final UserRepository userRepository;
    private final IdempotencyHelper idempotencyHelper;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            AccountService accountService,
            UserRepository userRepository,
            IdempotencyHelper idempotencyHelper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.idempotencyHelper = idempotencyHelper;
    }

    @Override
    public TransactionResponse deposit(
            Long accountId, DepositRequest request,
            String email, String idempotencyKey) {

        User user = getUser(email);

        return idempotencyHelper.executeIdempotently(
                idempotencyKey, user, TransactionResponse.class,
                () -> doDeposit(accountId, request, user));
    }



    @Transactional
    protected TransactionResponse doDeposit(
            Long accountId, DepositRequest request, User user) {

        Account account = accountService.getOwnedAccount(accountId, user);

        validateAccountActive(account);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);

        // @Version field is checked automatically by Hibernate
        // on THIS save() call — no manual code needed for that part
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(generateTransactionRef());
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    // ... withdraw, transfer, getTransactionHistory, and private
    @Override
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request, String email, String idempotencyKey) {
        return null;
    }

    @Override
    public TransactionResponse transfer(TransferRequest request, String email, String idempotencyKey) {
        return null;
    }

    @Override
    public Page<TransactionResponse> getTransactionHistory(Long accountId, String email, Pageable pageable) {
        return null;
    }
    //     helpers continue below
    private void validateAccountActive(Account account) {

        if (account.getAccountStatus() == AccountStatus.FROZEN) {
            throw new AccountOperationException(
                    "This account is frozen. Contact support.");
        }

        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountOperationException(
                    "This account has been closed.");
        }

        if (account.getAccountStatus() == AccountStatus.DORMANT) {
            throw new AccountOperationException(
                    "This account is dormant. Please reactivate it first.");
        }
    }
    private String generateTransactionRef() {
        String ref;
        do {
            ref = "TXN" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
        } while (transactionRepository.existsByTransactionRef(ref));

        return ref;
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionRef(transaction.getTransactionRef())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .relatedAccountNumber(
                        transaction.getRelatedAccount() != null
                                ? transaction.getRelatedAccount().getAccountNumber()
                                : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}