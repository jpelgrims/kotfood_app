CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY,
                                    store VARCHAR(250),
                                    name VARCHAR(250),
                                    price DECIMAL(10,2),
                                    weight DECIMAL(10,2),
                                    datetime TIMESTAMP);