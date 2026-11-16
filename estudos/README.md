# Trilha de estudos

Os temas estão na ordem em que entram no projeto: cada etapa usa o que a anterior construiu.
O material de estudo é por minha conta. Aqui ficam só a ordem e os desafios.

## Como funciona

1. Estudo o tema.
2. Faço os desafios no código do Mini Mercado (fácil → médio → difícil).
3. Peço a validação: a validação confere o código e roda o que o desafio pede.
4. Desafio validado → marco aqui na tabela.

Cada desafio tem um **"Validado quando"**: é o que precisa funcionar para ele contar.
O projeto precisa continuar funcionando ao final de cada etapa.

## Ordem

| #  | Tema                                                    | Por que nesta posição                                                        | Fácil | Médio | Difícil |
| -- | ------------------------------------------------------- | ---------------------------------------------------------------------------- | :---: | :---: | :-----: |
| 1  | [Dockerização](01-dockerizacao.md)                      | Base de tudo: o resto da trilha roda em containers                            |  [ ]  |  [ ]  |   [ ]   |
| 2  | [API Gateway](02-api-gateway.md)                        | Uma porta de entrada única antes de crescer o número de serviços              |  [ ]  |  [ ]  |   [ ]   |
| 3  | [Monitoramento Open Source](03-monitoramento-opensource.md) | Com tudo em containers, dá para subir Prometheus e Grafana ao lado        |  [ ]  |  [ ]  |   [ ]   |
| 4  | [OpenTelemetry](04-opentelemetry.md)                    | Completa o monitoramento com traces e logs, seguindo a requisição entre serviços |  [ ]  |  [ ]  |   [ ]   |
| 5  | [APM](05-apm.md)                                        | Usa as métricas e traces das etapas 3 e 4 para achar gargalos                  |  [ ]  |  [ ]  |   [ ]   |
| 6  | [Monitoramento e Acesso](06-monitoramento-e-acesso.md)  | Protege o que já existe: login, permissões e alertas                          |  [ ]  |  [ ]  |   [ ]   |
| 7  | [LocalStack](07-localstack.md)                          | Traz a "nuvem" para a máquina antes de usar serviços AWS                      |  [ ]  |  [ ]  |   [ ]   |
| 8  | [Desenvolvimento Serverless](08-serverless.md)          | Primeira função na "nuvem" local: pagamento assíncrono do pedido              |  [ ]  |  [ ]  |   [ ]   |
| 9  | [Terraform](09-terraform.md)                            | Transforma em código a infra AWS que foi criada na mão nas etapas 7 e 8        |  [ ]  |  [ ]  |   [ ]   |
| 10 | [Kubernetes](10-kubernetes.md)                          | Fechamento: leva o sistema inteiro para um cluster, com escala automática      |  [ ]  |  [ ]  |   [ ]   |

## Como o projeto evolui

```
Etapa 1-2   web ─► gateway ─► catalogo / pedidos ─► postgres          (tudo no Docker Compose)
Etapa 3-5   + prometheus, grafana, otel-collector, tempo/jaeger, loki   (ver o sistema por dentro)
Etapa 6     + login (JWT) no gateway e alertas
Etapa 7-9   pedidos ─► fila SQS ─► Lambda de pagamento ─► fila ─► pedidos   (LocalStack + Terraform)
Etapa 10    tudo isso rodando no Kubernetes
```

Temas novos que aparecerem depois entram no fim da tabela, na posição em que fizerem sentido.
