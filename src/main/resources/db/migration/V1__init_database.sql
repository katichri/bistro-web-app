-- V1__init_database.sql

CREATE TABLE IF NOT EXISTS "product" (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              name VARCHAR(255),
    price DOUBLE PRECISION,
    external_id VARCHAR(255),
    CONSTRAINT unique_external_id UNIQUE (external_id)
    );

CREATE TABLE IF NOT EXISTS "order" (
                                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       customer_name VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS "order_item" (
                                                 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                 product_id BIGINT,
                                                 quantity INT,
                                                 order_id BIGINT,
                                                 CONSTRAINT fk_product
                                                 FOREIGN KEY (product_id) REFERENCES "product"(id),
    CONSTRAINT fk_order
    FOREIGN KEY (order_id) REFERENCES "order"(id)
    );