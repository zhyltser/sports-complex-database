CREATE VIEW trainers_at_risk AS
    SELECT id_user AS id_trainer,
           name || ' ' || surname AS trainer_name,
           email,
           count(*) AS num_of_conducted_trainings
    FROM trainer t
        JOIN personal_data USING(id_user)
        JOIN conduction c ON c.id_trainer = t.id_user
    GROUP BY id_user, name, surname, email
    HAVING count(*) <= 10
    ORDER BY count(*), id_user
;

SELECT * FROM trainers_at_risk
