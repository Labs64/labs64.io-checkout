CREATE TABLE billing_info
(
    id           UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    tenant_id    TEXT                           NOT NULL,
    name         TEXT                           NOT NULL,
    email        TEXT,
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
CREATE UNIQUE INDEX ux_billing_info_tenant_id_id ON billing_info (tenant_id, id);

CREATE INDEX ix_billing_info_tenant_id ON billing_info (tenant_id);
CREATE INDEX ix_billing_info_email ON billing_info (tenant_id, email) WHERE email IS NOT NULL;
CREATE INDEX ix_billing_info_created_at ON billing_info (tenant_id, created_at);
CREATE INDEX ix_billing_info_updated_at ON billing_info (tenant_id, updated_at);
CREATE INDEX ix_billing_info_confirmed_at ON billing_info (tenant_id, confirmed_at);
CREATE INDEX ix_billing_info_extra_gin ON billing_info USING gin (extra jsonb_path_ops);