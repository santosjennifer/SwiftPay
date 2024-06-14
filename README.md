![Last Commit](https://img.shields.io/github/last-commit/santosjennifer/SwiftPay)
[![Continuous Integration with Github](https://github.com/santosjennifer/SwiftPay/actions/workflows/ci-maven.yml/badge.svg)](https://github.com/santosjennifer/SwiftPay/actions/workflows/ci-maven.yml)

# Serviço de Pagamentos SwiftPay
O SwiftPay é uma plataforma de pagamentos que permite depositar e realizar transferências de dinheiro entre usuários.

### Tecnologia:
- Java 21
- Maven
- Spring Boot 3.2.5
- Apache Kafka
- MySQL
- Docker

### Como executar o projeto:

1. Clone o repositório:
```
git clone git@github.com:santosjennifer/SwiftPay.git
```

2. Na raiz do projeto, execute o comando:
```
docker-compose up
```

3. Acesse o Swagger da aplicação:
```
http://localhost:8099/swagger-ui/index.html#/
```

### Swagger:
![image](https://github.com/santosjennifer/SwiftPay/assets/90192611/675effe8-c345-4371-a381-1de71b260486)


Transação de pagamento:
```http request
POST /api/transaction
{
    "value": 5.00,
    "payer": 2,
    "payee": 1
}
```

```http response
200 OK
{
    "id": "9f0425ff-9af3-4640-b7c1-85c750380fdb",
    "value": 5.00,
    "payer": 2,
    "payee": 1,
    "createdAt": "2024-05-16T18:39:50.490496789"
}
```
Cadastro de usuário (Consumidor ou Logista):
```http request
POST /api/user
{
    "name": "Gustavo Souza",
    "email": "gustavosouza@gmail.com",
    "document": "79461016000",
    "userType": "CONSUMER",
    "password": "12344",
    "balance": 50.00
}
```

```http response
200 OK
{
    "id": 2,
    "name": "Gustavo Souza",
    "document": "79461016000",
    "email": "gustavosouza@gmail.com",
    "userType": "CONSUMER",
    "balance": 50.00
}
```


Depósito:
```http request
POST /api/user/deposit
{
    "user": 1,
    "amount": 50.99
}
```

```http response
200 OK
{
    "user": 1,
    "amount": 50.99
}
```
