CREATE TABLE checkout_transaction
(
    id                UUID PRIMARY KEY,
    tenant_id         TEXT        NOT NULL,
    status            TEXT        NOT NULL,
    purchase_order_id UUID        NOT NULL REFERENCES purchase_order (id),
    billing_info      JSONB,
    shipping_info     JSONB,
    consents          JSONB       NOT NULL DEFAULT '{}'::jsonb,
    extra             JSONB       NOT NULL DEFAULT '{}'::jsonb,
    payment_method    TEXT        NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ NOT NULL,
    closed_at         TIMESTAMPTZ NULL
);

CREATE INDEX ix_ctx_po_id ON checkout_transaction (purchase_order_id);
CREATE INDEX ix_ctx_tenant ON checkout_transaction (tenant_id);
CREATE INDEX ix_ctx_tenant_status ON checkout_transaction (tenant_id, status);
CREATE INDEX ix_ctx_tenant_created_at ON checkout_transaction (tenant_id, created_at DESC);
CREATE INDEX ix_ctx_extra_gin ON checkout_transaction USING gin (extra jsonb_path_ops);

