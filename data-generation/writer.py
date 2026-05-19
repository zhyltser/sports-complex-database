import psycopg2
from psycopg2 import sql

class DatabaseWriter:
    def __init__(self, dbname, user, password, host, port):
        self.conn_params = {
            "dbname": dbname,
            "user": user,
            "password": password,
            "host": host,
            "port": port
        }

        self.num_of_insert = 0
        self.conn = None
        self.cursor = None

    def _connect(self):
        self.conn = psycopg2.connect(**self.conn_params)
        self.cursor = self.conn.cursor()

    def _disconnect(self):
        self.cursor.close()
        self.conn.close()

    # Generic method to insert data
    def _execute_insert(self, query, values, error_msg):
        try:
            self.cursor.execute(sql.SQL(query), values)
            self.num_of_insert += 1
            if(self.num_of_insert > 500):
                self.conn.commit()
                self.num_of_insert = 0
        except Exception as e:
            print(f"{error_msg}: {e}")

    def insert_sports_hall(self, hall_name, size):
        self._execute_insert("""
            INSERT INTO sports_hall (hall_name, size)
            VALUES (%s, %s)
        """, (hall_name, size), "Error inserting into sports_hall")

    def insert_hspecialization(self, specialization_name):
        self._execute_insert("""
            INSERT INTO hspecialization (specialization_name)
            VALUES (%s)
        """, (specialization_name,), "Error inserting into hspecialization")

    def insert_hall_specialization(self, id_sports_hall, id_specialization):
        self._execute_insert("""
            INSERT INTO hall_specialization (id_sports_hall, id_specialization)
            VALUES (%s, %s)
        """, (id_sports_hall, id_specialization), "Error inserting into hall_specialization")

    def insert_user(self, login, password):
        self._execute_insert("""
            INSERT INTO "user" (login, password)
            VALUES (%s, %s)
        """, (login, password), "Error inserting into user")

    def insert_client(self, id_user):
        self._execute_insert("""
            INSERT INTO client (id_user)
            VALUES (%s)
        """, (id_user,), "Error inserting into client")

    def insert_preference_in_sport(self, preference_name):
        self._execute_insert("""
            INSERT INTO preference_in_sport (preference_name)
            VALUES (%s)
        """, (preference_name,), "Error inserting into preference_in_sport")

    def insert_client_preference(self, id_client, id_preference):
        self._execute_insert("""
            INSERT INTO client_preference (id_client, id_preference)
            VALUES (%s, %s)
        """, (id_client, id_preference), "Error inserting into client_preference")

    def insert_personal_data(self, id_user, name, surname, date_of_birth, email, phone_number=None):
        self._execute_insert("""
            INSERT INTO personal_data (id_user, name, surname, date_of_birth, email, phone_number)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (id_user, name, surname, date_of_birth, email, phone_number), "Error inserting into personal_data")

    def insert_address(self, id_user, city, street, house_number):
        self._execute_insert("""
            INSERT INTO address (id_user, city, street, house_number)
            VALUES (%s, %s, %s, %s)
        """, (id_user, city, street, house_number), "Error inserting into address")

    def insert_trainer(self, id_user, education):
        self._execute_insert("""
            INSERT INTO trainer (id_user, education)
            VALUES (%s, %s)
        """, (id_user, education), "Error inserting into trainer")

    def insert_trainer_assistant(self, id_trainer, id_assistant):
        self._execute_insert("""
            INSERT INTO trainer_assistant (id_trainer, id_assistant)
            VALUES (%s, %s)
        """, (id_trainer, id_assistant), "Error inserting into trainer_assistant")

    def insert_tspecialization(self, specialization_name):
        self._execute_insert("""
            INSERT INTO tspecialization (specialization_name)
            VALUES (%s)
        """, (specialization_name,), "Error inserting into tspecialization")

    def insert_trainer_specialization(self, id_trainer, id_specialization):
        self._execute_insert("""
            INSERT INTO trainer_specialization (id_trainer, id_specialization)
            VALUES (%s, %s)
        """, (id_trainer, id_specialization), "Error inserting into trainer_specialization")

    def insert_training(self, training_date, id_sports_hall, type, capacity, price_for_one):
        self._execute_insert("""
            INSERT INTO training (training_date, id_sports_hall, type, capacity, price_for_one)
            VALUES (%s, %s, %s, %s, %s)
        """, (training_date, id_sports_hall, type, capacity, price_for_one), "Error inserting into training")

    def insert_conduction(self, id_trainer, id_training):
        self._execute_insert("""
            INSERT INTO conduction (id_trainer, id_training)
            VALUES (%s, %s)
        """, (id_trainer, id_training), "Error inserting into conduction")

    def insert_payment(self, final_price, payment_method, payment_date, id_client):
        self._execute_insert("""
            INSERT INTO payment (final_price, payment_method, payment_date, id_client)
            VALUES (%s, %s, %s, %s)
        """, (final_price, payment_method, payment_date, id_client), "Error inserting into payment")

    def insert_reservation(self, reservation_date, id_client, status, id_training, id_payment):
        self._execute_insert("""
            INSERT INTO reservation (reservation_date, id_client, status, id_training, id_payment)          
            VALUES (%s, %s, %s, %s, %s)
        """, (reservation_date, id_client, status, id_training, id_payment), "Error inserting into reservation")

    def update_reservation(self, id_reservation, id_payment):
        self._execute_insert("""
            UPDATE reservation
            SET id_payment = %s
            WHERE id_reservation = %s
        """, (id_payment, id_reservation), "Error updating reservation")







