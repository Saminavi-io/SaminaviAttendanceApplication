CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    employee_id VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE attendance (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER REFERENCES employees(id),
    clock_in_time TIMESTAMP,
    clock_out_time TIMESTAMP,
    date DATE NOT NULL
);


select * from employees;
