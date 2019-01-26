CREATE TABLE IF NOT EXISTS user (id VARCHAR(100) PRIMARY KEY);

CREATE TABLE IF NOT EXISTS ingredient (name VARCHAR(100) PRIMARY KEY,
                                        measurement VARCHAR(100),
                                        amount INTEGER NOT NULL,
                                        unit VARCHAR(20) NOT NULL,
                                        price DOUBLE NOT NULL);

CREATE TABLE IF NOT EXISTS shopping_list (id INTEGER PRIMARY KEY,
                                          portions INTEGER NOT NULL,
                                          calorie_limit INTEGER NOT NULL,
                                          budget INTEGER NOT NULL);

CREATE TABLE IF NOT EXISTS user_has_shopping_list (user_id STRING,
                                                    shopping_list_id INTEGER,
                                                    FOREIGN KEY (user_id) REFERENCES user(id),
                                                    FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS shopping_list_has_ingredients (shopping_list_id INTEGER,
                                                          ingredient_name VARCHAR(100),
                                                          purchased INTEGER DEFAULT 0,
                                                          FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                          FOREIGN KEY (ingredient_name) REFERENCES ingredient(name));


