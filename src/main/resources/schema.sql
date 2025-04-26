-- Database Schema for MOSCAT Multipurpose Cooperative Savings and Loan System

-- Users Table (administrators)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- SUPER_ADMIN, TREASURER, BOOKKEEPER
    status VARCHAR(20) NOT NULL, -- ACTIVE, INACTIVE
    full_name VARCHAR(100),
    email VARCHAR(100),
    contact_number VARCHAR(20),
    created_at DATE,
    last_login DATE
);

-- Members Table
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    age INT,
    birth_date DATE,
    present_address VARCHAR(255),
    permanent_address VARCHAR(255),
    contact_number VARCHAR(20),
    email VARCHAR(100),
    employer VARCHAR(100),
    employment_status VARCHAR(50), -- REGULAR, CONTRACT OF SERVICE, JOB ORDER
    gross_monthly_income DECIMAL(15, 2),
    avg_net_monthly_income DECIMAL(15, 2),
    status VARCHAR(20) NOT NULL, -- ACTIVE, DORMANT, CLOSED
    join_date DATE,
    last_activity_date DATE,
    loan_eligibility_amount DECIMAL(15, 2) DEFAULT 0
);

-- Savings Accounts Table
CREATE TABLE IF NOT EXISTS savings_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) DEFAULT 0,
    interest_earned DECIMAL(15, 2) DEFAULT 0,
    last_interest_computation_date DATE,
    status VARCHAR(20) NOT NULL, -- ACTIVE, DORMANT, CLOSED
    open_date DATE,
    last_activity_date DATE,
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- DEPOSIT, WITHDRAWAL, LOAN_PAYMENT, LOAN_RELEASE, INTEREST_EARNING
    amount DECIMAL(15, 2) NOT NULL,
    running_balance DECIMAL(15, 2) NOT NULL,
    reference_number VARCHAR(50),
    description VARCHAR(255),
    user_id INT NOT NULL, -- Who processed the transaction
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES savings_accounts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Interest Settings Table
CREATE TABLE IF NOT EXISTS interest_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    interest_rate DECIMAL(5, 2) NOT NULL,
    minimum_balance_for_interest DECIMAL(15, 2) NOT NULL,
    computation_method VARCHAR(20) NOT NULL, -- DAILY, MONTHLY
    effective_date DATE NOT NULL,
    change_reason VARCHAR(255),
    created_by INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Loan Types Table
CREATE TABLE IF NOT EXISTS loan_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    interest_rate DECIMAL(5, 2) NOT NULL,
    min_term_months INT NOT NULL,
    max_term_months INT NOT NULL,
    min_amount DECIMAL(15, 2) NOT NULL,
    max_amount DECIMAL(15, 2) NOT NULL,
    requires_rlpf BOOLEAN DEFAULT TRUE
);

-- Loans Table
CREATE TABLE IF NOT EXISTS loans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    loan_number VARCHAR(50) NOT NULL UNIQUE,
    loan_type VARCHAR(20) NOT NULL, -- REGULAR, PETTY_CASH, BONUS
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    term_years INT NOT NULL,
    previous_loan_balance DECIMAL(15, 2) DEFAULT 0,
    total_deductions DECIMAL(15, 2) DEFAULT 0,
    net_loan_proceeds DECIMAL(15, 2) NOT NULL,
    monthly_amortization DECIMAL(15, 2) NOT NULL,
    remaining_balance DECIMAL(15, 2) NOT NULL,
    application_date DATE,
    approval_date DATE,
    release_date DATE,
    maturity_date DATE,
    status VARCHAR(20) NOT NULL, -- PENDING, APPROVED, REJECTED, ACTIVE, PAID
    processed_by INT,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (processed_by) REFERENCES users(id)
);

-- Loan Deductions Table
CREATE TABLE IF NOT EXISTS loan_deductions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    percentage DECIMAL(5, 2) DEFAULT 0,
    is_rlpf BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);

-- Loan Amortization Schedule Table
CREATE TABLE IF NOT EXISTS loan_amortization (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    year INT NOT NULL,
    remaining_principal DECIMAL(15, 2) NOT NULL,
    annual_interest DECIMAL(15, 2) NOT NULL,
    annual_principal_payment DECIMAL(15, 2) NOT NULL,
    monthly_amortization DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);

-- Insert default loan types
INSERT INTO loan_types (code, name, description, interest_rate, min_term_months, max_term_months, min_amount, max_amount, requires_rlpf)
VALUES 
('REGULAR', 'Regular Loan', 'Standard loan for members', 12.0, 12, 60, 10000, 500000, TRUE),
('PETTY_CASH', 'Petty Cash Loan', 'Small, short-term loan', 9.0, 1, 12, 1000, 10000, FALSE),
('BONUS', 'Bonus Loan', 'Special loan for active members', 10.0, 12, 36, 5000, 100000, FALSE)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    description = VALUES(description);

-- Insert default interest settings (if no settings exist)
INSERT INTO interest_settings (interest_rate, minimum_balance_for_interest, computation_method, effective_date, change_reason, created_by)
SELECT 2.0, 1000.0, 'MONTHLY', CURRENT_DATE, 'Initial interest rate setting', 
    (SELECT id FROM users WHERE username = 'mmpcadmin' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM interest_settings);
