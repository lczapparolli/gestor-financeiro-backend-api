# API de backend do Gestor Financeiro

## Objetivo do projeto

O projeto como um todo tem como objetivo o estudo de recursos da linguagem Java, do framework Quarkus, da arquitetura de microsserviços e de componentes muito utilizados em plataformas de nuvem, como containers e a gestão de Kubernets. 

Os recursos e funcionalidades serão implementados para resolver problemas práticos, pensando na realidade pessoal do controle financeiro de uma família.

## Funcionalidades do serviço

Este serviço será a peça central do projeto oferecendo as funcionalidades para os frontends previstos (WebApp React e App Android). A stack será composta de uma aplicação Java, utilizando o framework Quarkus, que irá expor uma API Rest e armazenar os dados em um banco de dados MySQL.

## Modelagem de dados

Os dados estão organizados da seguinte forma:

- Entidade `Conta`: Representa as contas-correntes, poupanças e cartões de crédito
- Entidade `Categoria`: São os tipos de movimentação, agrupamentos para os movimentos
- Entidade `Previsao`: Utilizada para registrar o orçamento planejado por cada Categoria em um determinado mês
- Entidade `CartaoCredito`: Especialização da Conta, com configurações específicas para tratar do fechamento e vencimento das faturas
- Entidade `ContaPagar`: Representa os pagamentos a serem feitos, como forma de lembrete e planejamento
- Entidade `Fatura`: Especialização da ContaPagar, realizando o vínculo de um CartaoCredito
- Entidade `Movimento`: Entidade central, representando entradas e saídas das contas. Sendo a realização das previsões