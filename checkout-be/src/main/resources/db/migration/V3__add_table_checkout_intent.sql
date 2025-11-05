CREATE TABLE checkout_intent
(
    id                UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    tenant_id         TEXT                                   NOT NULL,
    status            TEXT                                   NOT NULL,
    amount            BIGINT                                 NOT NULL,
    currency          TEXT                                   NOT NULL,
    payment_method    TEXT,
    billing_info_id   UUID,
    extra             JSONB,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    closed_at         TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_checkout_intent__billing_info
        FOREIGN KEY (tenant_id, billing_info_id)
            REFERENCES billing_info (tenant_id, id)
            ON DELETE RESTRICT
);

-- unique
CREATE UNIQUE INDEX ux_checkout_intent_tenant_id_id ON checkout_intent (tenant_id, id);

CREATE INDEX idx_checkout_intent_tenant_id ON checkout_intent (tenant_id);
CREATE INDEX idx_checkout_intent_status ON checkout_intent (tenant_id, status);
CREATE INDEX idx_checkout_intent_created_at ON checkout_intent (tenant_id, created_at);
CREATE INDEX idx_checkout_intent_updated_at ON checkout_intent (tenant_id, updated_at);
CREATE INDEX idx_checkout_intent_tenant_billing ON checkout_intent (tenant_id, billing_info_id);
CREATE INDEX idx_checkout_intent_extra_gin ON checkout_intent USING gin (extra jsonb_path_ops);