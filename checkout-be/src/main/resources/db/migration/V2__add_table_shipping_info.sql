CREATE TABLE shipping_info
(
    id           UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    tenant_id    TEXT                                   NOT NULL,
    name         TEXT                                   NOT NULL,
    phone        TEXT,
    city         TEXT,
    country      TEXT,
    address1     TEXT,
    address2     TEXT,
    postal_code  TEXT,
    state        TEXT,
    extra        JSONB,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- unique idx
CREATE UNIQUE INDEX ux_shipping_info_tenant_id_id ON shipping_info (tenant_id, id);

CREATE INDEX idx_shipping_info_tenant_id ON shipping_info (tenant_id);
CREATE INDEX idx_shipping_info_created_at ON shipping_info (tenant_id, created_at);
CREATE INDEX idx_shipping_info_updated_at ON shipping_info (tenant_id, updated_at);
CREATE INDEX idx_shipping_info_confirmed_at ON shipping_info (tenant_id, confirmed_at);
CREATE INDEX idx_shipping_info_extra_gin ON shipping_info USING gin (extra jsonb_path_ops);