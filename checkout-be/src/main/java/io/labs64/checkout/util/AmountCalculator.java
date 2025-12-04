package io.labs64.checkout.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import io.labs64.checkout.entity.PurchaseOrderEntity;
import io.labs64.checkout.entity.PurchaseOrderItemEntity;
import io.labs64.checkout.model.Tax;
import io.labs64.checkout.model.TaxRateType;

public class AmountCalculator {

    public record Amounts(long netAmount, long taxAmount, long grossAmount) {
    }

    public AmountCalculator() {
    }

    public static Amounts calculate(final PurchaseOrderEntity order) {
        long netAmount = 0L;
        long taxAmount = 0L;
        long grossAmount = 0L;

        final List<PurchaseOrderItemEntity> items = order.getItems();

        if (items != null) {
            for (final PurchaseOrderItemEntity item : items) {
                final Amounts it = calculate(item);
                netAmount = Math.addExact(netAmount, it.netAmount());
                taxAmount = Math.addExact(taxAmount, it.taxAmount());
                grossAmount = Math.addExact(grossAmount, it.grossAmount());
            }
        }

        return new Amounts(netAmount, taxAmount, grossAmount);
    }

    public static Amounts calculate(final PurchaseOrderItemEntity item) {
        final long price = item.getPrice() == null ? 0L : item.getPrice();
        final int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
        final long netAmount = Math.multiplyExact(price, (long) quantity);

        long taxAmount = 0L;
        final Tax tax = item.getTax();

        if (tax != null && tax.getRateType() != null && tax.getRate() != null) {
            final TaxRateType rateType = tax.getRateType();
            final BigDecimal rate = tax.getRate();

            if (rateType == TaxRateType.FIXED) {
                taxAmount = rate.setScale(0, RoundingMode.UNNECESSARY).longValueExact();
            } else if (rateType == TaxRateType.PERCENTAGE) {
                taxAmount = BigDecimal.valueOf(netAmount).multiply(rate)
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP).longValueExact();
            }
        }

        final long gross = netAmount + taxAmount;
        return new Amounts(netAmount, taxAmount, gross);
    }

}
