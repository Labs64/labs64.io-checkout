package io.labs64.checkout.entity;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "shipping")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "checkoutIntent", "shippingInfo" })
public class ShippingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String tenantId;

    @NotBlank
    @Column(nullable = false)
    private String carrier;

    @NotBlank
    @Column(nullable = false)
    private String trackingNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> extra = new HashMap<>();

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_intent_id", nullable = false)
    private CheckoutIntentEntity checkoutIntent;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_info_id", nullable = false)
    private ShippingInfoEntity shippingInfo;

    @PrePersist
    void prePersist() {
        final OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
