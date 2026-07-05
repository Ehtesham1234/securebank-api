package com.ehtesham.securebank.loan.repository;

import com.ehtesham.securebank.loan.entity.EmiPayment;
import com.ehtesham.securebank.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmiPaymentRepository
        extends JpaRepository<EmiPayment, Long> {

    List<EmiPayment> findByLoanOrderByEmiNumberAsc(Loan loan);
}