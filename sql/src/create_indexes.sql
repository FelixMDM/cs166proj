DROP INDEX IF EXISTS users_login_hash_idx;
DROP INDEX IF EXISTS items_price_btree_idx;

CREATE INDEX users_login_hash_idx ON Users USING HASH (login);
CREATE INDEX items_price_btree_idx ON Items USING BTREE (price);
