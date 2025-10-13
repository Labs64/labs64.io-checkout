package io.labs64.checkout.v1.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.labs64.checkout.v1.model.CheckoutIntentStatus;
import io.labs64.checkout.v1.validator.ValidCurrency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "checkout_intent")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class CheckoutIntentEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @ValidCurrency
    private String currency;

    @Enumerated(EnumType.STRING)
    private CheckoutIntentStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_info_id")
    private BillingInfoEntity billingInfo;

    @OneToMany(mappedBy = "checkoutIntent", cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.LAZY)
    private List<ShippingEntity> shipping;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column(updatable = false)
    private OffsetDateTime closedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> extra = new HashMap<>();

    @PrePersist
    void prePersist() {
        final OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }

        if (status == null) {
            status = CheckoutIntentStatus.CREATED;
        }

        updatedAt = now;
        closingCheck();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
        closingCheck();
    }

    private void closingCheck() {
        final List<CheckoutIntentStatus> finalized = new ArrayList<>();
        finalized.add(CheckoutIntentStatus.FAILED);
        finalized.add(CheckoutIntentStatus.CANCELED);
        finalized.add(CheckoutIntentStatus.COMPLETED);

        if (finalized.contains(status)) {
            closedAt = OffsetDateTime.now();
        }
    }
}