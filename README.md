# PixKey API

## Visão Geral

Este projeto é uma API RESTful desenvolvida como solução para o **Desafio 1 - Inclusão, Alteração, Consulta e Exclusão de Chaves Pix**, proposto pelo Itaú. A aplicação implementa um sistema para gerenciamento de chaves Pix, seguindo as especificações do PDF fornecido, com suporte a inclusão, alteração, consulta e exclusão lógica de chaves, além de validações específicas para cada tipo de chave (`CPF`, `CNPJ`, `EMAIL`, `CELULAR`, `ALEATORIO`). O código foi estruturado utilizando a arquitetura **Ports and Adapters** (Hexagonal) e **Clean Architecture**, garantindo separação de responsabilidades, testabilidade e escalabilidade.

A API foi construída com **Java 17**, **Spring Boot** e **Maven**, utilizando **Spring Data JPA** para persistência em um banco **PostgreSQL**, e inclui testes de integração com **Testcontainers** para validar o comportamento em um ambiente realista. O objetivo foi atender **100% dos requisitos do desafio**, incluindo **validações de formato, limites de chaves por conta** (5 para PF, 20 para PJ), **unicidade de chaves** e **suporte a consultas paginadas**.

---

## Estrutura do Projeto

![img_1.png](img_1.png)


### Principais Componentes

1. **Camada de Domínio (`domain`)**: Foca nas regras de negócio puras, sem dependência de frameworks..
    - **`PixKey`**: Record que modela uma chave Pix com todos os campos obrigatórios (id, tipoChave, valorChave, etc.).
    - **`KeyType` e `AccountType`**: Enums para tipos de chave e conta.
    - **`exceptions`**: Exceções como `DuplicateKeyException`, `InvalidKeyException`, etc.
    - **`validation`**: Validadores (`CpfValidator`, `CnpjValidator`, etc.) para cada tipo de chave.

2. **Aplicação (`application`)**: Aqui ficam as regras de aplicação, implementadas com casos de uso e interfaces de entrada/saída.
    - **`ports.in`**: Interfaces dos casos de uso (`CreatePixKeyUseCase`, `SearchPixKeysUseCase`, etc.).
    - **`ports.out`**: Interface do repositório (`PixKeyRepository`).
    - **`usecases`**: Implementações dos casos de uso com a lógica de negócio.

3. **Infraestrutura (`infrastructure`)** Responsável por configurações técnicas, como persistência e integração.:
    - **`config`**: `ApplicationConfig` para configurar beans do Spring.
    - **`persistence`**: `PixKeyEntity` (entidade JPA) e `PixKeyJpaRepository` (interface JPA).

4. **Adaptadores (`adapters`)** Fornece interfaces para entrada e saída do sistema.:
    - **`inbound.rest`**: `PixKeyController` para endpoints REST.
    - **`inbound.dto`**: DTOs como `CreatePixKeyRequest` e `PixKeyResponse`.
    - **`outbound.persistence`**: `PixKeyRepositoryImpl` para conectar o repositório ao JPA.

---

## Pré-requisitos

- **Java 17**: Versão mínima para rodar o Spring Boot 3.
- **Maven**: Para gerenciar dependências e construir o projeto.
- **Docker**: Necessário para rodar o banco de dados via `docker-compose` e para Testcontainers (testes de integração).
- **Docker Compose**: Para iniciar o PostgreSQL com o arquivo `docker-compose.yml`.

---

## Como Executar

### 1. Configuração do Banco de Dados
O projeto inclui um arquivo `docker-compose.yml` para configurar e rodar o banco de dados PostgreSQL. Siga os passos abaixo:

**Inicie o banco: Na raiz do projeto, execute:** docker-compose up -d

### 2. Build e Execução
**Compile e execute:** mvn clean install  e mvn spring-boot:run

**A API estará disponível em** http://localhost:{SERVER_PORT}/api/v1/pix-keys.

### 3. Testes

O teste CreatePixKeyIntegrationTest usa Testcontainers para criar um PostgreSQL temporário e valida os requisitos do PDF. Da mesma forma, as classes UpdatePixKeyIntegrationTest, SearchPixKeyIntegrationTest e DeletePixKeyIntegrationTest também utilizam Testcontainers para criar containers PostgreSQL temporários, garantindo a validação dos requisitos de alteração, consulta e deleção de chaves Pix conforme especificado no documento. Nota: O Testcontainers ignora o docker-compose.yml e cria um container separado para cada execução de teste.

### 4. Endpoints da API
| Método | Endpoint               | Descrição                                      | Corpo da Requisição       | Resposta                     |
|--------|------------------------|-----------------------------------------------|---------------------------|------------------------------|
| POST   | `/api/v1/pix-keys`     | Cria uma nova chave Pix                       | `CreatePixKeyRequest`     | `PixKeyResponse` (200 OK)    |
| GET    | `/api/v1/pix-keys/{id}`| Consulta uma chave por ID                     | -                         | `PixKeyResponse` (200 OK)    |
| GET    | `/api/v1/pix-keys`     | Consulta chaves com filtros (paginada)        | Query params (filtros)    | `Page<PixKeyResponse>`       |
| PUT    | `/api/v1/pix-keys/{id}`| Altera uma chave existente                    | `UpdatePixKeyRequest`     | `PixKeyResponse` (200 OK)    |
| DELETE | `/api/v1/pix-keys/{id}`| Desativa uma chave (exclusão lógica)          | -                         | `PixKeyResponse` (200 OK)    |

### 5. Detalhes da Implementação
***Requisitos do PDF Atendidos***

***Inclusão de Chaves Pix:***

***Campos:*** Todos os campos da Tabela 1 estão em PixKey e PixKeyEntity.

***Validações:***

    Unicidade: Verificada em CreatePixKeyUseCaseImpl via existsByKeyValue.
    
    Limites: 5 chaves para PF (CPF, CELULAR, EMAIL) e 20 para PJ (CNPJ) em CreatePixKeyUseCaseImpl.
    
    Formato: Validadores específicos (CpfValidator, CnpjValidator, etc.) em domain.validation.
    
    Implementação: PixKeyController.createPixKey chama o caso de uso e persiste via PixKeyRepositoryImpl.
    
***Alteração de Chaves Pix:***

    Campos Alteráveis: Apenas tipoConta, numeroAgencia, numeroConta, nomeCorrentista, sobrenomeCorrentista são permitidos em UpdatePixKeyRequest.

    Restrições: Só altera chaves ativas, verificado em PixKeyController.updatePixKey.

    Implementação: UpdatePixKeyUseCaseImpl atualiza e salva.

***Exclusão de Chaves Pix:***
    
    Lógica: Marca active = false e define deactivated_at via trigger no banco.

    Restrições: Só desativa chaves ativas, verificado em PixKeyController.deactivatePixKey.

    Implementação: DeactivatePixKeyUseCaseImpl realiza a exclusão lógica.

***Consulta de Chaves Pix:***

    Filtros: Suporta todos os filtros da Tabela 5 em SearchPixKeysQuery.
    
    Restrições: Proíbe combinação de dataInclusao e dataInativacao em PixKeyController.getPixKeys.
    
    Paginação: Até 20 resultados por página via Pageable.
    
    Implementação: SearchPixKeysUseCaseImpl usa PixKeyRepositoryImpl com Specification.

***Validações Gerais:***

    Formatos: Implementados nos validadores (CpfValidator, CelularValidator, etc.).

    Limites: valorChave (77), nomeCorrentista (30), sobrenomeCorrentista (45), etc., validados via anotações em DTOs.