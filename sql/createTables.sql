-- Tables for Pokemon Database

CREATE TABLE `Pokemon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ownerid` int(11) DEFAULT NULL,
  `location` int(11) DEFAULT NULL,
  `nickName` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `totalhp` int(11) DEFAULT NULL,
  `currenthp` int(11) DEFAULT NULL,
  `attack` int(11) DEFAULT NULL,
  `defense` int(11) DEFAULT NULL,
  `speed` int(11) DEFAULT NULL,
  `special` int(11) DEFAULT NULL,
  `idno` int(11) DEFAULT NULL,
  `EXP` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `species` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `PokemonItems` (
  `ownerid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `description` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `usee` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `number` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  PRIMARY KEY (`ownerid`)
);

CREATE TABLE `PokemonMoves` (
  `pokemonid` int(11) NOT NULL,
  `name` varchar(30) COLLATE latin1_general_ci DEFAULT NULL,
  `description` varchar(30) COLLATE latin1_general_ci DEFAULT NULL,
  `effect` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `type` varchar(20) COLLATE latin1_general_ci DEFAULT NULL,
  `currentpp` int(11) DEFAULT NULL,
  `pp` int(11) DEFAULT NULL,
  `dmg` int(11) DEFAULT NULL,
  `accuracy` int(11) DEFAULT NULL
);

CREATE TABLE `PokemonUsers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `UserName` varchar(30) COLLATE latin1_general_ci DEFAULT NULL,
  `Password` varchar(30) COLLATE latin1_general_ci DEFAULT NULL,
  `whoknows` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `dir` int(11) DEFAULT NULL,
  `picture` varchar(12) COLLATE latin1_general_ci DEFAULT NULL,
  `lpcx` int(11) DEFAULT NULL,
  `lpcy` int(11) DEFAULT NULL,
  `lpclevel` int(11) DEFAULT NULL,
  `money` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
