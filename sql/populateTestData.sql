-- Test data to populate DB with

-- Pokemon table test data

INSERT INTO `Pokemon` VALUES(1, 1, 0, 'Pika', 7, NULL, 3, 66, 45, 82, 35, 302, 1064, 0, 'Mankey');
INSERT INTO `Pokemon` VALUES(2, 2, 0, 'Pika', 6, NULL, 16, 66, 45, 82, 35, 302, 1064, 0, 'Pikachu');
INSERT INTO `Pokemon` VALUES(3, 3, 0, 'Pikachu', 5, NULL, 20, 66, 56, 82, 33, 333, 1045, 0, 'Pikachu');
INSERT INTO `Pokemon` VALUES(4, 1, 1, 'Pikachu', 5, NULL, 20, 66, 56, 82, 33, 333, 1045, 0, 'Pikachu');

-- PokemonItems table test data

INSERT INTO `PokemonItems` VALUES(1, 'Pokeball', 'Catches Pokemon', 'Catching Pokemon', '7');
INSERT INTO `PokemonItems` VALUES(2, 'Pokeball', 'Catches Pokemon', 'Catching Pokemon', '3');
INSERT INTO `PokemonItems` VALUES(3, 'Pokeball', 'Catches Pokemon', 'Catching Pokemon', '7');

-- PokemonMoves table test data

INSERT INTO `PokemonMoves` VALUES(1, 'Scratch', 'Scratch', 'None', 'Normal', 20, 20, 20, 20);
INSERT INTO `PokemonMoves` VALUES(1, 'Pound', 'Pound', 'None', 'Normal', 20, 20, 20, 20);
INSERT INTO `PokemonMoves` VALUES(2, 'Pound', 'Pound', 'None', 'Normal', 20, 20, 20, 20);
INSERT INTO `PokemonMoves` VALUES(3, 'Pound', 'Pound', 'None', 'Normal', 20, 20, 20, 20);
INSERT INTO `PokemonMoves` VALUES(4, 'Pound', 'Pound', 'None', 'Normal', 20, 20, 20, 20);

-- PokemonUsers table test data

INSERT INTO `PokemonUsers` VALUES(1, 'DrYerzinia', 'DrYerzinia', NULL, 3, 3, 1, 1, 'Char', 2, 2, 1, 10000);
INSERT INTO `PokemonUsers` VALUES(2, 'Abbey', 'Abbey', NULL, 0, 0, 23, 2, 'Mom', -1, -1, 23, 10000);
INSERT INTO `PokemonUsers` VALUES(3, 'Laura', 'Laura', NULL, 21, 18, 16, 3, 'Lady1', 2, 2, 1, 10000);
