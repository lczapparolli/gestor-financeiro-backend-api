CREATE TABLE `CONTA` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) NOT NULL,
  `ativo` bool NOT NULL DEFAULT 1,
  `data_criacao` datetime(6) NOT NULL,
  `versao` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `CATEGORIA` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) NOT NULL,
  `ativo` bool NOT NULL DEFAULT 1,
  `data_criacao` datetime(6) NOT NULL,
  `versao` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `PREVISAO` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `periodo` date NOT NULL,
  `id_categoria` bigint NOT NULL,
  `valor` decimal(19,2) NOT NULL,
  `ativo` bool NOT NULL DEFAULT 1,
  `data_criacao` datetime(6) NOT NULL,
  `versao` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_previsao_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `CATEGORIA` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `CONTA_PAGAR` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `valor` decimal(19,2) DEFAULT NULL,
  `vencimento` date DEFAULT NULL,
  `periodo` date NOT NULL,
  `id_categoria` bigint NOT NULL,
  `ativo` bool NOT NULL DEFAULT 1,
  `data_criacao` datetime(6) NOT NULL,
  `versao` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_contapagar_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `CATEGORIA` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `MOVIMENTO` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `valor` decimal(19,2) NOT NULL,
  `id_categoria` bigint NOT NULL,
  `id_conta` bigint NOT NULL,
  `periodo` date NOT NULL,
  `id_contapagar` bigint DEFAULT NULL,
  `ativo` bool NOT NULL DEFAULT 1,
  `data_criacao` datetime(6) NOT NULL,
  `versao` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_movimento_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `CATEGORIA` (`id`),
  CONSTRAINT `fk_movimento_conta` FOREIGN KEY (`id_conta`) REFERENCES `CONTA` (`id`),
  CONSTRAINT `fk_movimento_contapagar` FOREIGN KEY (`id_contapagar`) REFERENCES `CONTA_PAGAR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `CARTAO_CREDITO` (
  `id` bigint NOT NULL,
  `dia_fechamento` int DEFAULT NULL,
  `dia_vencimento` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_cartaocredito_conta` FOREIGN KEY (`id`) REFERENCES `CONTA` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `FATURA` (
  `id` bigint NOT NULL,
  `id_cartao_credito` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_fatura_contapagar` FOREIGN KEY (`id`) REFERENCES `CONTA_PAGAR` (`id`),
  CONSTRAINT `fk_fatura_cartaocredito` FOREIGN KEY (`id_cartao_credito`) REFERENCES `CARTAO_CREDITO` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;