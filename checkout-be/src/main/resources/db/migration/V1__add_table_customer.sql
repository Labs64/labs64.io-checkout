CREATE TABLE customer
(
    id         UUID PRIMARY KEY,
    tenant_id  TEXT        NOT NULL,
    first_name TEXT,
    last_name  TEXT        NOT NULL,
    email      TEXT,
    phone      TEXT,
    billing_info      JSONB,
    shipping_info     JSONB,
    extra      JSONB       NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX ux_cus_tenant_email ON customer (tenant_id, email) WHERE email IS NOT NULL;
CREATE INDEX ix_cus_tenant_id ON customer (tenant_id);
CREATE INDEX ix_cus_tenant_created_at ON customer (tenant_id, created_at DESC);
CREATE INDEX ix_cus_tenant_last_name  ON customer (tenant_id, last_name);
CREATE INDEX ix_cus_tenant_email      ON customer (tenant_id, email);
CREATE INDEX ix_cus_extra_gin ON customer USING gin (extra jsonb_path_ops);
