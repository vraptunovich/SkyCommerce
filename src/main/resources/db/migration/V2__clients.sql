INSERT INTO clients (id, client_type, first_name, last_name, company_name, vat_number, registration_number,
                     annual_revenue)
VALUES ('C_IND_001', 'INDIVIDUAL', 'John', 'Doe', NULL, NULL, NULL, NULL);

INSERT INTO clients (id, client_type, first_name, last_name, company_name, vat_number, registration_number,
                     annual_revenue)
VALUES ('C_IND_002', 'INDIVIDUAL', 'Anna', 'Smith', NULL, NULL, NULL, NULL);

INSERT INTO clients (id, client_type, first_name, last_name, company_name, vat_number, registration_number,
                     annual_revenue)
VALUES ('C_PRO_LOW_001', 'PROFESSIONAL', NULL, NULL, 'Acme Solutions', 'EU123456789', 'REG-LOW-001', 5000000.00);

INSERT INTO clients (id, client_type, first_name, last_name, company_name, vat_number, registration_number,
                     annual_revenue)
VALUES ('C_PRO_HIGH_001', 'PROFESSIONAL', NULL, NULL, 'MegaCorp International', 'EU987654321', 'REG-HIGH-001',
        15000000.00);
