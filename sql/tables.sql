DROP TABLE IF EXISTS sports_hall CASCADE;
DROP TABLE IF EXISTS hspecialization CASCADE;
DROP TABLE IF EXISTS hall_specialization CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS preference_in_sport CASCADE;
DROP TABLE IF EXISTS client_preference CASCADE;
DROP TABLE IF EXISTS personal_data CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS trainer CASCADE;
DROP TABLE IF EXISTS trainer_assistant CASCADE;
DROP TABLE IF EXISTS tspecialization CASCADE;
DROP TABLE IF EXISTS trainer_specialization CASCADE;
DROP TABLE IF EXISTS training CASCADE;
DROP TABLE IF EXISTS conduction CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS reservation_payment CASCADE;


CREATE TABLE sports_hall (
    id_sports_hall      serial      PRIMARY KEY,
    hall_name           varchar(64) NOT NULL UNIQUE,
    size                smallint    NOT NULL
                                    CHECK (size BETWEEN 1 AND 3)
                                    DEFAULT 1
);

CREATE TABLE hspecialization (
    id_specialization   serial      PRIMARY KEY,
    specialization_name varchar(64) NOT NULL UNIQUE
);

CREATE TABLE hall_specialization (
    id_sports_hall      int4        NOT NULL REFERENCES sports_hall
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    id_specialization   int4        NOT NULL REFERENCES hspecialization
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    PRIMARY KEY (id_sports_hall, id_specialization)
);

CREATE TABLE "user" (
    id_user             serial      PRIMARY KEY,
    login               varchar(64) NOT NULL UNIQUE,
    password            bytea       NOT NULL
);

CREATE TABLE client (
    id_user             int4        PRIMARY KEY REFERENCES "user"
                                    ON DELETE NO ACTION
                                    ON UPDATE CASCADE
);

CREATE TABLE preference_in_sport (
    id_preference       serial      PRIMARY KEY,
    preference_name     varchar(64) NOT NULL UNIQUE
);

CREATE TABLE client_preference (
    id_client           int4        NOT NULL REFERENCES client (id_user)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    id_preference       int4        NOT NULL REFERENCES preference_in_sport
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    PRIMARY KEY (id_client, id_preference)
);

CREATE TABLE personal_data (
    id_user             int4        PRIMARY KEY REFERENCES "user"
                                    ON DELETE NO ACTION
                                    ON UPDATE CASCADE,
    name                varchar(64) NOT NULL,
    surname             varchar(64) NOT NULL,
    date_of_birth       date        NOT NULL CHECK (
                                                    EXTRACT(year FROM date_of_birth)
                                                    BETWEEN EXTRACT(year FROM CURRENT_DATE) - 100
                                                    AND EXTRACT(year FROM CURRENT_DATE)
                                                    ),
    email               varchar(64) NOT NULL UNIQUE,
    phone_number        varchar(18) DEFAULT NULL,
    UNIQUE(name, surname, date_of_birth)
);

CREATE TABLE address (
    id_user             int4        PRIMARY KEY REFERENCES "user"
                                    ON DELETE NO ACTION
                                    ON UPDATE CASCADE,
    city                varchar(64) NOT NULL,
    street              varchar(64) NOT NULL,
    house_number        varchar(16) NOT NULL
);

CREATE TABLE trainer (
    id_user             int4        PRIMARY KEY REFERENCES "user"
                                    ON DELETE NO ACTION
                                    ON UPDATE CASCADE,
    education           bool        NOT NULL DEFAULT false
);

CREATE TABLE trainer_assistant (
    id_trainer          int4        NOT NULL REFERENCES trainer (id_user)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    id_assistant        int4        CHECK (id_assistant <> id_trainer)
                                    REFERENCES trainer (id_user)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    PRIMARY KEY (id_trainer, id_assistant)
);

CREATE TABLE tspecialization (
    id_specialization   serial      PRIMARY KEY,
    specialization_name varchar(64) NOT NULL UNIQUE
);

CREATE TABLE trainer_specialization (
    id_trainer          int4        NOT NULL REFERENCES trainer (id_user)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    id_specialization   int4        NOT NULL REFERENCES tspecialization
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    PRIMARY KEY (id_trainer, id_specialization)
);

CREATE TABLE training (
    id_training         serial      PRIMARY KEY,
    training_date       timestamp(0) NOT NULL CHECK (
                                                     EXTRACT(year FROM training_date)
                                                     BETWEEN EXTRACT(year FROM CURRENT_DATE) - 1
                                                     AND EXTRACT(year FROM CURRENT_DATE) + 1
                                                     ),
    id_sports_hall      int4        REFERENCES sports_hall
                                    ON DELETE SET NULL
                                    ON UPDATE CASCADE,
    type                varchar(32) NOT NULL,
    capacity            int2        NOT NULL,
    price_for_one       money       NOT NULL,
    UNIQUE (id_sports_hall, training_date)
);

CREATE TABLE conduction (
    id_trainer          int4        DEFAULT NULL REFERENCES trainer (id_user)
                                    ON DELETE SET DEFAULT
                                    ON UPDATE CASCADE,
    id_training         int4        NOT NULL REFERENCES training
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    PRIMARY KEY (id_trainer, id_training)
);

CREATE TABLE payment (
    id_payment          serial      PRIMARY KEY,
    final_price         money       NOT NULL DEFAULT 0,
    payment_method      varchar(16) NOT NULL
                                    CHECK (payment_method IN ('card', 'cash')),
    payment_date        timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    CHECK (payment_date <= CURRENT_TIMESTAMP),
    id_client           int4        NOT NULL REFERENCES client (id_user)
                                    ON DELETE RESTRICT
                                    ON UPDATE CASCADE,
    UNIQUE (id_client, payment_date)
);

CREATE TABLE reservation (
    id_reservation      serial      PRIMARY KEY,
    reservation_date    timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    CHECK (reservation_date <= CURRENT_TIMESTAMP),
    id_client           int4        NOT NULL REFERENCES client (id_user)
                                    ON DELETE RESTRICT
                                    ON UPDATE CASCADE,
    status              varchar(16) NOT NULL DEFAULT 'reserved'
                                    CHECK (status IN ('reserved', 'in process', 'payed')),
    id_training         int4        NOT NULL REFERENCES training
                                    ON DELETE RESTRICT
                                    ON UPDATE CASCADE,
    id_payment          int4        DEFAULT NULL REFERENCES payment
                                    ON DELETE SET DEFAULT
                                    ON UPDATE CASCADE,
    UNIQUE (id_client, reservation_date)
);




































































