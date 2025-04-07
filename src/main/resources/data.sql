-- Insert users
INSERT INTO users (id, email, username, fullname, password, avatar_url, phone_number)
VALUES 
  (1, 'chelsea@example.com', 'chelsea', 'Chelsea Emmanuela', 'password123', NULL, '08123456789'),
  (2, 'giz@example.com', 'giz', 'Gizmo', 'password123', NULL, '08123456788');

-- Insert wallets
INSERT INTO wallets (id, user_id, account_number, balance, created_at, updated_at)
VALUES 
  (1, 1, '100899', 3000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 2, '100900', 2000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert top-up transactions
INSERT INTO transactions (id, wallet_id, transaction_type, amount, recipient_wallet_id, transaction_date, description)
VALUES 
  (1, 1, 'TOP_UP', 1000.00, NULL, CURRENT_TIMESTAMP, 'Initial deposit'),
  (2, 1, 'TOP_UP', 2000.00, NULL, CURRENT_TIMESTAMP, 'Top up via BYOND PAY'),
  (3, 1, 'TOP_UP', 1500.00, NULL, CURRENT_TIMESTAMP, 'Top up via gopay'),
  (4, 2, 'TOP_UP', 1000.00, NULL, CURRENT_TIMESTAMP, 'Top-up via QR');

-- Insert transfer transactions
INSERT INTO transactions (id, wallet_id, transaction_type, amount, recipient_wallet_id, transaction_date, description)
VALUES 
  (5, 1, 'TRANSFER', 250.00, 2, CURRENT_TIMESTAMP, 'Lunch money'),
  (6, 1, 'TRANSFER', 250.00, 2, CURRENT_TIMESTAMP, 'Debts'),
  (7, 2, 'TRANSFER', 250.00, 1, CURRENT_TIMESTAMP, 'Split bill');