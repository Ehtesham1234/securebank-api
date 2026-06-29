package com.ehtesham.securebank.transaction.controller;

import com.ehtesham.securebank.common.response.ApiResponse;
import com.ehtesham.securebank.security.service.CustomUserPrincipal;
import com.ehtesham.securebank.transaction.dto.DepositRequest;
import com.ehtesham.securebank.transaction.dto.TransactionResponse;
import com.ehtesham.securebank.transaction.dto.TransferRequest;
import com.ehtesham.securebank.transaction.dto.WithdrawRequest;
import com.ehtesham.securebank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {


    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/accounts/{accountId}/deposit")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        TransactionResponse response = transactionService.deposit(
                accountId, request, principal.getUsername(), idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody WithdrawRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        TransactionResponse response = transactionService.withdraw(
                accountId, request, principal.getUsername(), idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", response));
    }

    @PostMapping("/transactions/transfer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        TransactionResponse response = transactionService.transfer(
                request, principal.getUsername(), idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful", response));
    }
}
