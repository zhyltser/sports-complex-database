# Sports Complex Database System

A full-stack database project for managing a sports complex — from relational schema design to a Java/JPA application layer.

## Tech Stack

- **PostgreSQL** — relational database
- **Java 23 + JPA/Hibernate 6** — ORM application layer
- **Python + Faker** — test data generation
- **Maven**

## Project Structure

| Folder | Contents |
|---|---|
| `sql/` | Schema, queries, triggers, views, indexes, transactions |
| `data-generation/` | Python scripts to populate DB with realistic fake data |
| `jpa-app/` | Java application with generic DAO pattern over the schema |

## Database Schema

18 tables covering: Users, Clients & Trainers via inheritance, Sports Halls, Trainings, Reservations, Payments, Specializations.

Key features:

- Table inheritance: `user` → `client` / `trainer`
- Many-to-Many relationships: trainings ↔ trainers, halls ↔ specializations, clients ↔ sport preferences
- Trigger: age validation before trainer insert
- View: `trainers_at_risk` — trainers with low activity
- Indexes for query performance optimization
- Transaction: full payment flow with rollback on error

## Setup

1. Create a PostgreSQL database
2. Run `sql/tables.sql` to create the schema
3. Run `data-generation/my_faker.py` to populate with test data
4. Configure `jpa-app/src/main/resources/META-INF/persistence.xml`
5. Run the Java app:

```bash
cd jpa-app
mvn compile exec:java
