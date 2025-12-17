CREATE TABLE purchase_order_item
(
    id                UUID PRIMARY KEY,
    purchase_order_id UUID        NOT NULL REFERENCES purchase_order (id) ON DELETE CASCADE,
    name              TEXT        NOT NULL,
    description       TEXT,
    url               TEXT,
    image             TEXT,
    sku               TEXT,
    uom               TEXT,
    quantity          INTEGER     NOT NULL CHECK (quantity > 0),
    price             BIGINT      NOT NULL CHECK (price >= 0),
    tax               JSONB,
    extra             JSONB       NOT NULL DEFAULT '{}'::jsonb,
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ NOT NULL
);

CREATE INDEX ix_poi_purchase_order ON purchase_order_item (purchase_order_id);
CREATE INDEX ix_poi_sku ON purchase_order_item (sku);
CREATE INDEX ix_poi_extra_gin ON purchase_order_item USING gin (extra jsonb_path_ops);
CREATE INDEX ix_poi_tax_gin ON purchase_order_item USING gin (tax jsonb_path_ops);
