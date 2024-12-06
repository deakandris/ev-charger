package deakandris.evcharger.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record Cost(BigDecimal amount, Currency currency) {
}
