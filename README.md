# Mini Mercado

Um mini mercado com catálogo de produtos, carrinho e pedidos, dividido em microsserviços.
Projeto de estudo: a base é simples para receber, aos poucos, Docker, API Gateway, monitoramento, OpenTelemetry,
APM, LocalStack, Serverless, Terraform e Kubernetes. A ordem e os desafios de cada tema estão em [`estudos/`](estudos/).

- `web`: front-end em HTML e JavaScript
- `service/catalogo`: API de produtos e estoque, em Java com Spring Boot (porta 8081)
- `service/pedidos`: API de pedidos, em Java com Spring Boot (porta 8082)

## Como rodar

Com o Docker Desktop aberto, ligue o banco (container `minimercado-db`, veja [Banco de dados](#banco-de-dados)):

```
docker start minimercado-db
```

Em um terminal, suba o catálogo:

```
cd service\catalogo
.\mvnw.cmd spring-boot:run
```

Em outro, suba os pedidos:

```
cd service\pedidos
.\mvnw.cmd spring-boot:run
```

Depois abra o arquivo `web/index.html` no navegador.

Para testar a API sem a tela, use o [`requisicoes.http`](requisicoes.http) (extensão REST Client do VS Code).

## Como os serviços conversam

1. A tela lista os produtos direto do **catálogo** (`GET /api/produtos`).
2. Ao finalizar a compra, a tela chama **pedidos** (`POST /api/pedidos`).
3. Para cada item, o serviço de pedidos consulta o produto no catálogo (nome e preço) e pede a baixa no estoque.
4. Se a baixa de algum item falhar (por exemplo, estoque insuficiente), o estoque dos itens já baixados é devolvido e o pedido não é criado.
5. Ao cancelar um pedido, o estoque de todos os itens é devolvido ao catálogo.

Cada serviço tem o seu próprio banco. O pedido guarda só o id do produto e copia o nome e o preço no momento da compra.

## API

| Serviço  | Método e rota                            | O que faz                    |
| -------- | ---------------------------------------- | ---------------------------- |
| catalogo | `GET /api/produtos?categoria=`           | Lista produtos               |
| catalogo | `GET /api/produtos/{id}`                 | Busca um produto             |
| catalogo | `POST /api/produtos`                     | Cria um produto              |
| catalogo | `PUT /api/produtos/{id}`                 | Atualiza um produto          |
| catalogo | `DELETE /api/produtos/{id}`              | Remove um produto            |
| catalogo | `POST /api/produtos/{id}/baixa-estoque`  | Tira do estoque              |
| catalogo | `POST /api/produtos/{id}/repor-estoque`  | Devolve ao estoque           |
| pedidos  | `GET /api/pedidos`                       | Lista pedidos                |
| pedidos  | `GET /api/pedidos/{id}`                  | Busca um pedido              |
| pedidos  | `POST /api/pedidos`                      | Cria um pedido               |
| pedidos  | `PATCH /api/pedidos/{id}/cancelar`       | Cancela e devolve o estoque  |

## Organização do backend

Cada serviço em `service/` é um projeto Spring Boot separado, com a mesma organização.
Cada funcionalidade tem uma classe por pasta. O nome leva a funcionalidade e o sufixo da pasta, exceto na model:

| Pasta        | Responsabilidade     | Exemplo             |
| ------------ | -------------------- | ------------------- |
| `controller` | Chamada da API       | `ProdutoController` |
| `service`    | Processamento        | `ProdutoService`    |
| `repository` | Acesso ao banco, JPA | `ProdutoRepository` |
| `model`      | Tabelas do banco     | `Produto`           |

## Configuração

Tudo que muda entre ambientes vem de variável de ambiente:

| Variável       | Serviço  | Padrão                                     |
| -------------- | -------- | ------------------------------------------ |
| `PORT`         | os dois  | 8081 / 8082                                |
| `CATALOGO_URL` | pedidos  | `http://localhost:8081`                    |
| `DB_URL`       | os dois  | `jdbc:postgresql://localhost:5433/catalogo` ou `/pedidos` |
| `DB_USERNAME`  | os dois  | `minimercado`                              |
| `DB_PASSWORD`  | os dois  | `minimercado` (só para o ambiente local)    |
| `DB_POOL_SIZE` | os dois  | 10                                         |

## Banco de dados

PostgreSQL 16 rodando no Docker, no container `minimercado-db` (porta **5433** da máquina, para não conflitar com outros Postgres na 5432).
Um servidor só, com **um banco para cada serviço**: `catalogo` e `pedidos`. As tabelas são criadas pelo Hibernate (`ddl-auto=update`)
e o catálogo cadastra 10 produtos na primeira vez que sobe.

O container foi criado assim (só é preciso fazer uma vez):

```
docker run -d --name minimercado-db -e POSTGRES_USER=minimercado -e POSTGRES_PASSWORD=minimercado -e POSTGRES_DB=catalogo -p 5433:5432 -v minimercado-db-dados:/var/lib/postgresql/data postgres:16-alpine
docker exec minimercado-db psql -U minimercado -d catalogo -c "CREATE DATABASE pedidos;"
```

Os dados ficam no volume `minimercado-db-dados`, então sobrevivem a `docker stop` / `docker rm` do container.

Comandos úteis:

```
docker start minimercado-db                                  # liga
docker stop minimercado-db                                   # desliga
docker exec -it minimercado-db psql -U minimercado -d catalogo   # abre o psql (o comando \dt lista as tabelas)
```

Os testes automáticos (`mvnw test`) usam H2 em memória e não precisam do Docker.
