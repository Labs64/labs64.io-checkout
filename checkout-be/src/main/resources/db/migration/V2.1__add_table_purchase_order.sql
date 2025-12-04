CREATE TABLE purchase_order
(
    id          UUID PRIMARY KEY,
    tenant_id   TEXT        NOT NULL,
    customer_id UUID        NOT NULL REFERENCES customer (id),
    currency    TEXT        NOT NULL,
    extra       JSONB       NOT NULL DEFAULT '{}'::jsonb,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    starts_at   TIMESTAMPTZ,
    ends_at     TIMESTAMPTZ,
    CHECK (starts_at IS NULL OR ends_at IS NULL OR ends_at > starts_at)
);

CREATE INDEX ix_po_tenant ON purchase_order (tenant_id);
CREATE INDEX ix_po_customer ON purchase_order (customer_id);
CREATE INDEX ix_po_starts_at ON purchase_order (starts_at);
CREATE INDEX ix_po_created_at ON purchase_order (created_at DESC);
CREATE INDEX ix_po_ends_at ON purchase_order (ends_at);
CREATE INDEX ix_po_starts_at_ends_at ON purchase_order (starts_at, ends_at);
CREATE INDEX ix_po_extra_gin ON purchase_order USING gin (extra jsonb_path_ops);
