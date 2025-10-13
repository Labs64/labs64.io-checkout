CREATE TABLE shipping
(
    id                 UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    tenant_id          TEXT                                   NOT NULL,
    carrier            TEXT                                   NOT NULL,
    tracking_number    TEXT                                   NOT NULL,
    shipping_info_id   UUID                                   NOT NULL,
    checkout_intent_id UUID                                   NOT NULL,
    extra              JSONB,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

    CONSTRAINT fk_shipping__checkout_intent
        FOREIGN KEY (tenant_id, checkout_intent_id)
            REFERENCES checkout_intent (tenant_id, id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_shipping__shipping_info
        FOREIGN KEY (tenant_id, shipping_info_id)
            REFERENCES shipping_info (tenant_id, id)
            ON DELETE RESTRICT
);
-- unique idx
CREATE UNIQUE INDEX ux_shipping_tenant_id_id ON shipping (tenant_id, id);

CREATE INDEX idx_shipping_tenant_id ON shipping (tenant_id);
CREATE INDEX idx_shipping_tenant_id_tracking_number ON shipping (tenant_id, tracking_number);
CREATE INDEX idx_shipping_tenant_id_checkout_intent_id ON shipping (tenant_id, checkout_intent_id);
CREATE INDEX idx_shipping_tenant_id_shipping_info_id ON shipping (tenant_id, shipping_info_id);
CREATE INDEX idx_shipping_created_at ON shipping (tenant_id, created_at);
CREATE INDEX idx_shipping_updated_at ON shipping (tenant_id, updated_at);
CREATE INDEX idx_shipping_extra_gin ON shipping USING gin (extra jsonb_path_ops);