WITH inserted_user AS (
    INSERT INTO "user" (login, password)
        VALUES ('coolSem', '12345')
        RETURNING id_user
),
inserted_personal_data AS (
    INSERT INTO personal_data (id_user, name, surname, date_of_birth, email, phone_number)
        SELECT id_user, 'Sem', 'Smith', '2015-01-01', 'ssmith@gmail.com', '+420000000001'
        FROM inserted_user
),
inserted_client AS (
    INSERT INTO client (id_user)
        SELECT id_user FROM inserted_user
),
inserted_training AS (
    INSERT INTO training (training_date, id_sports_hall, type, capacity, price_for_one)
        SELECT CURRENT_DATE + INTERVAL '2 months' + TIME '09:30', 1, 'volleyball without trainer', 12, 150
        RETURNING id_training
)
INSERT INTO reservation (reservation_date, id_client, status, id_training, id_payment)
    VALUES (CURRENT_TIMESTAMP,
            (SELECT id_user FROM inserted_user), 'reserved',
            (SELECT id_training FROM inserted_training),NULL)
;

--
DELETE FROM client
WHERE id_user = 32006
;
DELETE FROM personal_data
WHERE id_user = 32006
;
DELETE FROM "user"
WHERE id_user = 32006
;

DELETE FROM reservation
WHERE id_client = 32006
;

DELETE FROM payment
WHERE id_client = 32006
;

DELETE FROM training
WHERE id_training IN (2701, 2702)
;


CREATE FUNCTION update_reservation_status(id_reserv INTEGER, id_user INTEGER, pay_method varchar(16), pay_date TIMESTAMP)
    RETURNS VOID AS $$
DECLARE
    reserv_status VARCHAR(16);
    price MONEY;
    id_paym INTEGER;
BEGIN
    reserv_status := (SELECT status
                      FROM reservation r
                      WHERE r.id_reservation = id_reserv);

    price := (SELECT price_for_one
              FROM training
                       JOIN reservation r USING(id_training)
              WHERE r.id_reservation = id_reserv);

    IF reserv_status NOT IN ('reserved') THEN
        RAISE EXCEPTION 'Reservation is already payed';
    END IF;

    IF pay_method NOT IN ('cash', 'card') THEN
        RAISE EXCEPTION 'Wrong payment method';
    END IF;

    INSERT INTO payment (final_price, payment_method, payment_date, id_client)
    VALUES (price, pay_method, pay_date, id_user)
    RETURNING id_payment INTO id_paym;

    UPDATE reservation r
    SET id_payment = id_paym,
        status = 'payed'
    WHERE id_reservation = id_reserv;
END;
$$ LANGUAGE plpgsql
;

BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT update_reservation_status((SELECT id_reservation FROM reservation ORDER BY id_reservation DESC LIMIT 1),
                                 (SELECT id_user FROM "user" ORDER BY id_user DESC LIMIT 1),
                                 'card'::VARCHAR(16), CURRENT_TIMESTAMP::TIMESTAMP - TIME '00:30');
COMMIT TRANSACTION;
;
