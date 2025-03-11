/* Replace the location to where you saved the data files*/
COPY Users
FROM 'cs166proj/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM 'cs166proj/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM 'cs166proj/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM 'cs166proj/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM 'cs166proj/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
