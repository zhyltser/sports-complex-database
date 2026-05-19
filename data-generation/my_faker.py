from faker import Faker
import random
from datetime import *
from unidecode import unidecode

from writer import DatabaseWriter


class MyFaker:
    SPECIALIZATIONS = ['volleyball', 'basketball', 'football', 'tennis', 'judo', 'karate', 'MMA', 'sambo',
                       'yoga', 'pilates', 'fitness', 'badminton', 'handball', 'futsal', 'squash', 'wrestling',
                       'gymnastics', 'aerobics', 'zumba', 'piloxing', 'bodybuilding', 'kickboxing', 'table tennis',
                       'dance', 'cheerleading', 'martial arts', 'taekwondo', 'fencing', 'rowing', 'indoor cycling',
                       'crossfit', 'weightlifting', 'boxing', 'dodgeball', 'floorball', 'parquet ball', 'indoor archery']
    SPEC_LEN = len(SPECIALIZATIONS)

    PREFERENCES = ['team sports', 'solo sports', 'with ball', 'without ball', 'indoor sports', 'combat sports',
                   'fitness and strength training', 'flexibility and balance', 'endurance sports', 'extreme sports',
                   'aerobic exercises', 'mind-body sports', 'competitive sports']
    PREF_LEN = len(PREFERENCES)

    PRICES_FOR_ONE = {1 : (250, 500),
                      2 : (200, 400),
                      3 : (150, 300)}

    MAX_CAPACITIES = {1 : 14,
                      2 : 28,
                      3 : 42}

    TRAINING_TYPES = ['with trainer', 'without trainer']
    TYPES_LEN = len(TRAINING_TYPES)

    RESERVATIONS_STATUS = ['reserved', 'in process', 'payed']

    PAYMENTS_METHODS = ['cash', 'card']

    def __init__(self, db, user, password, host, port,
                 num_users, num_trainers, num_halls):
        self.num_users = num_users
        self.num_trainers = num_trainers
        self.num_halls = num_halls

        # fakers
        self.fake_global = Faker()
        self.fake_cz = Faker('cz_CZ')

        # database writer
        self.writer = DatabaseWriter(db, user, password, host, port)

        # PRIMARY KEYS
        self.trainer_assistant_PK = [[] for _ in range(self.num_trainers)]
        self.trainer_spec_PK = [[] for _ in range(self.num_trainers)]
        self.client_pref_PK = [[] for _ in range(self.num_users)]
        self.hall_spec_PK = [[] for _ in range(self.num_halls)]

        # UNIQUE
        self.login_UQ = set()
        self.name_sur_dob_UQ = set()
        self.email_UQ = set()
        self.hall_name_UQ = set()
        self.idcl_paymdate_UQ = set()

        self.spec_trainer = [[] for _ in range(self.SPEC_LEN)]

        self.hall_sizes = []

        self.dates = self.gen_dates()
        self.days_num = 30
        self.one_day_num_train = 9
        self.total_slots = self.one_day_num_train * self.days_num

        self.trainer_occupancy = [[False for _ in range(self.total_slots)] for _ in range(self.num_trainers)]
        self.clients_reservations = set()

        self.clients_reservations_for_pay = [[] for _ in range(self.num_users)]

    # Function to generate password
    def gen_password(self):
        return self.fake_cz.password()

    # Function to generate Czech name depends on gender
    def gen_name(self, gender):
        if gender == 'male':
            return self.fake_cz.first_name_male()
        else:
            return self.fake_cz.first_name_female()

    # Function to generate Czech surname depends on gender
    def gen_surname(self, gender):
        if gender == 'male':
            return self.fake_cz.last_name_male()
        else:
            return self.fake_cz.last_name_female()

    # Function to generate date of birth (interval [-99 years; -10 years] from cur date)
    def gen_date_of_birth(self):
        return self.fake_cz.date_between(start_date="-99y", end_date="-10y")

    # Function to generate unique combination of name, surname, date of birth
    def gen_name_surname_dob_UQ(self):
        gender = random.choice(['male', 'female'])

        while True:
            name = self.gen_name(gender)
            surname = self.gen_surname(gender)
            date_of_birth = self.gen_date_of_birth()

            key = (name, surname, date_of_birth)
            if key not in self.name_sur_dob_UQ:
                self.name_sur_dob_UQ.add(key)
                return key

    # Function to generate unique email
    def gen_email_UQ(self):
        while True:
            email = self.fake_cz.email()
            if email not in self.email_UQ:
                self.email_UQ.add(email)
                return email

    # Function to generate phone number
    def gen_phone_number(self):
        phone_number = self.fake_cz.phone_number()
        if phone_number[0] == '0':
            phone_number = '+' + phone_number[2:]
        return phone_number

    # Function to generate Czech city
    def gen_city(self):
        return self.fake_cz.city()

    # Function to generate Czech street
    def gen_street(self):
        return self.fake_cz.street_name()

    # Function to generate house number
    def gen_house_number(self):
        return random.randint(1, 200)

    # Function to generate fake users information
    def gen_fake_user_inf(self):
        for user_id in range(1, self.num_users + 1):
            name, surname, date_of_birth = self.gen_name_surname_dob_UQ()
            login = unidecode(name + surname).lower() + str(date_of_birth).replace('-', '')
            password = self.gen_password()
            email = login + '@gmail.com'
            phone_number = self.gen_phone_number()
            city = self.gen_city()
            street = self.gen_street()
            house_number = self.gen_house_number()

            self.writer.insert_user(login, password)
            self.writer.insert_personal_data(user_id, name, surname, date_of_birth, email, phone_number)
            self.writer.insert_address(user_id, city, street, house_number)

    # Function to separate trainers and clients (first self.num_trainers users are trainers)
    def separate_trainers_clients(self):
        cur_num_tr = 0
        for user_id in range(1, self.num_users + 1):
            if cur_num_tr < self.num_trainers:
                education = random.choice(['TRUE', 'FALSE'])
                self.writer.insert_trainer(user_id, education)
                cur_num_tr += 1
            else:
                self.writer.insert_client(user_id)

    # Function to generate possible trainer specializations
    def gen_tspecialization(self):
        for spec in self.SPECIALIZATIONS:
            self.writer.insert_tspecialization(spec)

    # Function to gen specializations for all trainers
    def gen_trainer_specialization(self):
        for trainer_id in range(self.num_trainers):
            num_specs = random.randint(1, 6)
            spec_ids = random.sample(range(self.SPEC_LEN), num_specs)

            for spec_id in spec_ids:
                self.writer.insert_trainer_specialization(trainer_id + 1, spec_id + 1)
                self.trainer_spec_PK[trainer_id].append(spec_id)
                self.spec_trainer[spec_id].append(trainer_id)

    # Function to generate all possible preferences in sports
    def gen_preference_in_sport(self):
        for pref in self.PREFERENCES:
            self.writer.insert_preference_in_sport(pref)

    # Function to generate preferences for all clients
    def gen_client_preference(self):
        for client_id in range(self.num_trainers, self.num_users):
            num_prefs = random.randint(0, 5)
            if num_prefs == 0:
                continue

            pref_ids = random.sample(range(self.PREF_LEN), min(num_prefs, self.PREF_LEN))

            for pref_id in pref_ids:
                if pref_id not in self.client_pref_PK[client_id]:
                    self.writer.insert_client_preference(client_id + 1, pref_id + 1)
                    self.client_pref_PK[client_id].append(pref_id)

    # Function to generate assistants for trainers
    def gen_trainer_assistant(self):
        for trainer_id in range(self.num_trainers):
            num_assistants = random.randint(0, 5)
            if num_assistants == 0:
                continue

            possible_assistants = []
            trainer_specs = set(self.trainer_spec_PK[trainer_id])

            for assistant_id in range(self.num_trainers):
                if assistant_id == trainer_id:
                    continue
                assistant_specs = set(self.trainer_spec_PK[assistant_id])
                if trainer_specs & assistant_specs:
                    possible_assistants.append(assistant_id)

            if not possible_assistants:
                continue

            assistants = random.sample(possible_assistants, min(num_assistants, len(possible_assistants)))

            for assistant_id in assistants:
                if assistant_id not in self.trainer_assistant_PK[trainer_id]:
                    self.writer.insert_trainer_assistant(trainer_id + 1, assistant_id + 1)
                    self.trainer_assistant_PK[trainer_id].append(assistant_id)

    # Function to generate unique halls name
    def gen_hall_name_UQ(self):
        while True:
            hall_name = self.fake_global.first_name_female()
            if hall_name not in self.hall_name_UQ:
                self.hall_name_UQ.add(hall_name)
                return hall_name

    # Function to generate sport halls
    def gen_sports_hall(self):
        for _ in range(self.num_halls):
            size = random.randint(1, 3)
            hall_name = self.gen_hall_name_UQ()
            self.hall_sizes.append(size)
            self.writer.insert_sports_hall(hall_name, size)

    # Function to generate all possible hall specializations
    def gen_hspecialization(self):
        for spec in self.SPECIALIZATIONS:
            self.writer.insert_hspecialization(spec)

    # Function to generate specializations for all halls
    def gen_hall_specialization(self):
        for hall_id in range(self.num_halls):
            num_specializations = random.randint(1, 6)

            spec_ids = random.sample(range(self.SPEC_LEN), num_specializations)

            for spec_id in spec_ids:
                if spec_id not in self.hall_spec_PK[hall_id]:
                    self.writer.insert_hall_specialization(hall_id + 1, spec_id + 1)
                    self.hall_spec_PK[hall_id].append(spec_id)

    # Function to generate dates slots on 15 days back and 15 days in future
    def gen_dates(self):
        past_days = 15
        future_days = 15
        interval = timedelta(minutes=90)
        slots = []

        start_date = datetime.today().date() - timedelta(days=past_days)
        total_days = past_days + future_days + 1

        for day_offset in range(total_days):
            current_date = start_date + timedelta(days=day_offset)
            time = timedelta(hours=9)
            while time <= timedelta(hours=22, minutes=30):
                dt = datetime.combine(current_date, datetime.min.time()) + time
                slots.append(dt)
                time += interval

        return slots

    # Function to find trainers for training
    def find_trainers(self, time_slot, training_spec):
        trainers = []
        for trainer_id in self.spec_trainer[training_spec]:
            if self.trainer_occupancy[trainer_id][time_slot] == False:
                trainers.append(trainer_id)
                assist_num = min(1, random.randint(0, len(self.trainer_assistant_PK[trainer_id])))
                if assist_num > 0:
                    for assist_id in self.trainer_assistant_PK[trainer_id]:
                        if training_spec in self.trainer_spec_PK[assist_id] and self.trainer_occupancy[assist_id][time_slot] == False\
                            and assist_id != trainer_id:
                            trainers.append(assist_id)
                            break
                return trainers
        return None

    # Function to generate reservations for training
    def gen_reservation_for_training(self, date_id, price, training_id, capacity, reservation_id):
        for _ in range(capacity):
            client_id = random.randint(self.num_trainers, self.num_users - 1)
            reservation_date = self.dates[date_id] if self.dates[date_id] < datetime.today() else datetime.today()
            delta = timedelta(days=random.randint(1, 9), hours=random.randint(0, 23), minutes = random.randint(0, 59))
            reservation_date -= delta
            key = (client_id, reservation_date.month, reservation_date.day, reservation_date.hour, reservation_date.minute)
            if key in self.clients_reservations:
                continue

            reservation_status = None
            if self.dates[date_id] < datetime.today():
                reservation_status = 'payed'
            else:
                reservation_status = random.choice(self.RESERVATIONS_STATUS)

            if reservation_status != 'reserved':
                new_client_id = client_id
                if random.random() >= 0.5:
                    new_client_id = random.randint(num_trainers, self.num_users - 1)
                self.clients_reservations_for_pay[new_client_id].append((reservation_id, price, reservation_date, date_id))

            self.writer.insert_reservation(reservation_date, client_id + 1, reservation_status, training_id, None)
            reservation_id += 1
            self.clients_reservations.add(key)

    # Function to generate payments
    def gen_payments(self):
        payment_id = 1
        for client_id in range(self.num_trainers, self.num_users):
            len_res_for_pay = len(self.clients_reservations_for_pay[client_id])
            for res_for_pay in range(len_res_for_pay):
                reservations = []
                reservation_id, price, reservation_date, date_id = self.clients_reservations_for_pay[client_id][res_for_pay]
                reservations.append(reservation_id)

                max_res_date = reservation_date
                min_train_date = self.dates[date_id]

                if res_for_pay < len_res_for_pay - 1:
                    next_reservation_id, next_price, next_reservation_date, next_date_id = self.clients_reservations_for_pay[client_id][res_for_pay + 1]
                    if reservation_date < self.dates[next_date_id] and next_reservation_date < self.dates[date_id]:
                        reservations.append(next_reservation_id)
                        max_res_date = max(max_res_date, next_reservation_date)
                        min_train_date = min(min_train_date, self.dates[next_date_id])
                        price += next_price
                        res_for_pay += 1

                payment_method = random.choice(self.PAYMENTS_METHODS)
                payment_date = datetime.fromtimestamp((max_res_date.timestamp() + min_train_date.timestamp()) / 2)
                if payment_date >= datetime.today():
                    payment_date = datetime.fromtimestamp((max_res_date.timestamp() + datetime.today().timestamp()) / 2)

                while (client_id, payment_date.month, payment_date.day, payment_date.hour, payment_date.minute) in self.idcl_paymdate_UQ:
                    payment_date += timedelta(minutes=1)

                self.writer.insert_payment(price, payment_method, payment_date, client_id + 1)

                for reserv_id in reservations:
                    self.writer.update_reservation(reserv_id, payment_id)

                self.idcl_paymdate_UQ.add((client_id, payment_date.month, payment_date.day, payment_date.hour, payment_date.minute))
                payment_id += 1

    # Function to generate trainings
    def gen_training(self):
        training_id = 1
        reservation_id = 1
        for time_slot in range(self.one_day_num_train * self.days_num):
            for hall_id in range(self.num_halls):
                training_type = random.choice(self.TRAINING_TYPES)
                training_spec = random.choice(self.hall_spec_PK[hall_id])
                hall_size = self.hall_sizes[hall_id]
                capacity = self.MAX_CAPACITIES[hall_size]
                price_for_one = None
                if training_type == 'without trainer':
                    price_for_one = self.PRICES_FOR_ONE[hall_size][0]
                    self.writer.insert_training(self.dates[time_slot], hall_id + 1, self.SPECIALIZATIONS[training_spec] + " " + training_type, capacity, price_for_one)
                else:
                    trainers = self.find_trainers(time_slot, training_spec)
                    if trainers is None:
                        training_type = 'without trainer'
                        price_for_one = self.PRICES_FOR_ONE[hall_size][0]
                        self.writer.insert_training(self.dates[time_slot], hall_id + 1, self.SPECIALIZATIONS[training_spec] + " " + training_type, capacity, price_for_one)
                    else:
                        price_for_one = self.PRICES_FOR_ONE[hall_size][1]
                        self.writer.insert_training(self.dates[time_slot], hall_id + 1, self.SPECIALIZATIONS[training_spec] + " " + training_type, capacity, price_for_one)
                        for trainer_id in trainers:
                            self.trainer_occupancy[trainer_id][time_slot] = True
                            self.writer.insert_conduction(trainer_id + 1, training_id)
                self.gen_reservation_for_training(time_slot, price_for_one, training_id, capacity, reservation_id)
                reservation_id += capacity
                training_id += 1

if __name__ == '__main__':
    num_users = 32000
    num_trainers = 100
    num_halls = 10

    faker = MyFaker('your_db', 'username', 'password', 'localhost', '5432',
                     num_users, num_trainers, num_halls)
    faker.writer._connect()
    faker.gen_fake_user_inf()
    print('fake users generated')
    faker.separate_trainers_clients()
    print('trainers and clients separated')
    faker.gen_tspecialization()
    print('tspecializations generated')
    faker.gen_trainer_specialization()
    print('trainer_specializations generated')
    faker.gen_preference_in_sport()
    print('preference_in_sport generated')
    faker.gen_client_preference()
    print('client_preference generated')
    faker.gen_trainer_assistant()
    print('trainer_assistant generated')
    faker.gen_sports_hall()
    print('sports_hall generated')
    faker.gen_hspecialization()
    print('hspecialization generated')
    faker.gen_hall_specialization()
    print('hall_specialization generated')
    faker.gen_training()
    print('trainings generated')
    faker.gen_payments()
    print('payments generated')
    faker.writer.conn.commit()
    faker.writer._disconnect()


