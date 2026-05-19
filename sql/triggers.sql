WITH inserted_user AS (
    INSERT INTO "user" (login, password)
        VALUES ('coolJohn', '12345')
        RETURNING id_user
)

INSERT INTO personal_data (id_user, name, surname, date_of_birth, email, phone_number)
    SELECT id_user, 'John', 'Smith', '2010-01-01', 'jsmith@gmail.com', '+420000000002'
    FROM inserted_user
;

INSERT INTO trainer (id_user, education)
VALUES ((SELECT id_user FROM "user" ORDER BY id_user DESC LIMIT 1), FALSE)
;

DELETE FROM trainer
WHERE id_user = 32009
;
DELETE FROM personal_data
WHERE id_user = 32009
;
DELETE FROM "user"
WHERE id_user = 32009
;

CREATE FUNCTION check_trainer_age()
    RETURNS TRIGGER AS $$
DECLARE
    dob timestamp(0);
BEGIN
    dob := (SELECT date_of_birth
            FROM personal_data
            WHERE (id_user = NEW.id_user));

    IF EXTRACT (YEAR FROM age(dob)) < 13 THEN
        RAISE EXCEPTION 'Trainer must be at least 13 years old';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql
;

CREATE TRIGGER trainer_trigger
    BEFORE INSERT ON trainer
    FOR EACH ROW
    EXECUTE FUNCTION check_trainer_age()
;


