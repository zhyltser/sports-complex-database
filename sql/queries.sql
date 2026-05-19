-- INNER JOIN
SELECT DISTINCT name || ' ' || surname AS trainer_name,
       specialization_name
    FROM trainer t
        JOIN personal_data USING(id_user)
        JOIN trainer_specialization tr_spec ON t.id_user = tr_spec.id_trainer
        JOIN tspecialization tspec ON tr_spec.id_specialization = tspec.id_specialization
    LIMIT 10
;

SELECT city,
       count(*) AS num_of_users
    FROM "user"
        JOIN address addr USING(id_user)
    GROUP BY city
    ORDER BY num_of_users DESC
    LIMIT 10
;

-- OUTER JOIN
SELECT pd_trainer.name || ' ' || pd_trainer.surname AS trainer_name,
       pd_assistant.name || ' ' || pd_assistant.surname AS assistant_name
    FROM trainer t
        JOIN personal_data pd_trainer USING(id_user)
        LEFT JOIN trainer_assistant train_assist ON t.id_user = train_assist.id_trainer
        LEFT JOIN personal_data pd_assistant ON train_assist.id_assistant = pd_assistant.id_user
    ORDER BY assistant_name DESC
    LIMIT 30
;

-- condition
SELECT DISTINCT name || ' ' || surname AS trainer_name,
       email
    FROM trainer t
        JOIN personal_data USING(id_user)
        JOIN trainer_specialization tr_spec ON t.id_user = tr_spec.id_trainer
        JOIN tspecialization tspec USING(id_specialization)
    WHERE tspec.specialization_name IN ('volleyball')
;

-- aggregation
SELECT name || ' ' || surname AS trainer_name,
       count(*) AS num_of_conducted_trainings
    FROM trainer t
        JOIN personal_data pd USING(id_user)
        JOIN conduction c ON c.id_trainer = t.id_user
    GROUP BY trainer_name
    ORDER BY num_of_conducted_trainings
    LIMIT 10
;

-- aggregation + HAVING
SELECT login,
       count(r.id_reservation) AS num_of_reservations,
       sum(p.final_price) AS sum_of_payments
    FROM "user"
        JOIN client c USING(id_user)
        JOIN reservation r ON r.id_client = c.id_user
        JOIN payment p ON p.id_client = c.id_user
    GROUP BY login
    HAVING sum(p.final_price::numeric) < 5000
    ORDER BY num_of_reservations DESC, sum_of_payments
    LIMIT 10
;

SELECT hall_name,
       count(CASE WHEN tr.type LIKE '% with trainer' THEN 1 END) AS num_of_trainings_with_trainer,
       count(CASE WHEN tr.type LIKE '% without trainer' THEN 1 END) AS num_of_trainings_without_trainer
    FROM sports_hall
         JOIN training tr USING(id_sports_hall)
    GROUP BY hall_name
    ORDER BY num_of_trainings_with_trainer DESC, num_of_trainings_without_trainer DESC
    LIMIT 3
;

SELECT hall_name,
       count(CASE WHEN tr.type LIKE '% with trainer' THEN 1 END) AS num_of_trainings_with_trainer,
       count(CASE WHEN tr.type LIKE '% without trainer' THEN 1 END) AS num_of_trainings_without_trainer
    FROM sports_hall
         JOIN training tr USING(id_sports_hall)
    GROUP BY hall_name
    ORDER BY num_of_trainings_with_trainer DESC, num_of_trainings_without_trainer DESC
    OFFSET 3
;

-- UNION
SELECT name || ' ' || surname AS client_name
    FROM client c
         JOIN personal_data USING(id_user)
         JOIN client_preference cl_pref ON cl_pref.id_client = c.id_user
         JOIN preference_in_sport pref USING(id_preference)
    WHERE preference_name = 'combat sports'

UNION

SELECT name || ' ' || surname AS client_name
    FROM client c
         JOIN personal_data USING(id_user)
         JOIN client_preference cl_pref ON cl_pref.id_client = c.id_user
         JOIN preference_in_sport pref USING(id_preference)
    WHERE preference_name = 'competitive sports'
LIMIT 10
;


-- INTERSECT
SELECT name || ' ' || surname AS name_surname
    FROM client
        JOIN personal_data USING(id_user)

INTERSECT

SELECT name || ' ' || surname AS name_surname
    FROM trainer
         JOIN personal_data USING(id_user)
ORDER BY name_surname
LIMIT 10
;

-- EXCEPT
SELECT login AS client_login,
       name || ' ' || surname AS client_name
    FROM client
        JOIN "user" USING(id_user)
        JOIN personal_data USING(id_user)

EXCEPT

SELECT login AS client_login,
       name || ' ' || surname AS client_name
    FROM client AS c
        JOIN "user" USING(id_user)
        JOIN personal_data USING(id_user)
        JOIN client_preference AS cl_pref ON cl_pref.id_client = c.id_user
LIMIT 10
;

-- nested SELECT
SELECT name || ' ' || surname AS client_name,
       count (id_reservation) AS num_of_reservations
    FROM client c
         JOIN personal_data USING(id_user)
         JOIN reservation r ON c.id_user = r.id_client
    GROUP BY client_name
    HAVING count (id_reservation) > (
        SELECT AVG (res_count)
        FROM (
             SELECT count (id_reservation) AS res_count
             FROM reservation
             GROUP BY id_client
            ) AS num_of_reservations)
    ORDER BY num_of_reservations DESC
LIMIT 10
;

SELECT count(*) AS num_of_reservations
    FROM reservation r WHERE r.status IN ('payed', 'in process')
;

SELECT count(*) AS num_of_payments
    FROM payment
;

ALTER TABLE training
    ALTER COLUMN price_for_one TYPE numeric(10, 2)
        USING REPLACE(price_for_one::text, '$', '')::numeric;
;
ALTER TABLE payment
    ALTER COLUMN final_price TYPE numeric(10, 2)
        USING REPLACE(REPLACE(final_price::text, '$', ''), ',', '')::numeric;







