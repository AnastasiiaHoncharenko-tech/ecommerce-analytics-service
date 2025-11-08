CREATE TABLE products
(
    id       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    price    NUMERIC      NOT NULL,
    category VARCHAR(255) NOT NULL
);

CREATE TABLE customers
(
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    join_date  DATE         NOT NULL
);

CREATE TABLE order_statuses
(
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE orders
(
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id INT,
    order_date  DATE NOT NULL,
    status_id   INT  NOT NULL,
    CONSTRAINT fk_customer_id
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE SET NULL,
    CONSTRAINT fk_status_name
        FOREIGN KEY (status_id) REFERENCES order_statuses (id) ON DELETE RESTRICT
);

CREATE TABLE order_items
(
    id                INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id          INT     NOT NULL,
    product_id        INT,
    quantity          INT     NOT NULL,
    price_at_purchase NUMERIC NOT NULL,
    CONSTRAINT fk_order_id
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_id
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
);
