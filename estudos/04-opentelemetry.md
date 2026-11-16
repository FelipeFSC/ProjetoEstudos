# 4. OpenTelemetry

**Objetivo da etapa:** seguir uma compra do começo ao fim, atravessando os serviços.

## Fácil: primeiro trace
Instrumentar os dois serviços e enviar os traces para um backend de traces (Jaeger ou Tempo).

**Validado quando:**
- Criar um pedido gera **um único trace** com os spans do `pedidos` e as chamadas ao `catalogo` dentro dele.
- Cada serviço aparece com o seu nome correto (`service.name`).

## Médio: Collector e correlação
Passar tudo por um OpenTelemetry Collector.

**Validado quando:**
- Os serviços enviam só para o Collector; é o Collector que distribui para os backends.
- As queries no banco aparecem como spans dentro do trace.
- O `trace_id` aparece nas linhas de log, e dá para ir do log para o trace (e vice-versa) no Grafana.

## Difícil: instrumentação de negócio
Spans e atributos que contam a história do pedido.

**Validado quando:**
- Existe um span próprio para a criação do pedido com atributos úteis (id do pedido, quantidade de itens, total), sem dados pessoais.
- Quando falta estoque, o span fica marcado como erro e mostra a exceção.
- A devolução de estoque (compensação) aparece no trace como uma etapa visível.
- O gateway (etapa 2) também participa do mesmo trace.
