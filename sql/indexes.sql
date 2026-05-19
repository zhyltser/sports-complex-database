CREATE INDEX idx_name_first_char ON personal_data (LEFT(name, 1));

EXPLAIN ANALYZE
SELECT name || ' ' || surname AS user_name
FROM personal_data
WHERE LEFT(name, 1) = 'Y'
;