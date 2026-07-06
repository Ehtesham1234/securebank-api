package com.ehtesham.securebank.card.repository;

import com.ehtesham.securebank.card.entity.Card;
import com.ehtesham.securebank.common.enums.CardStatus;
import com.ehtesham.securebank.common.enums.CardType;
import com.ehtesham.securebank.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository
        extends JpaRepository<Card, Long> {

    List<Card> findByUser(User user);

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByUserAndCardType(User user, CardType cardType);

    List<Card> findByStatusAndCardType(
            CardStatus status, CardType cardType);
}