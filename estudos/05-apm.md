# 5. APM

**Objetivo da etapa:** medir a performance do ponto de vista de quem usa e encontrar o gargalo.

## Fácil: teste de carga
Criar um teste de carga (k6, Gatling, JMeter...) que navega e compra.

**Validado quando:**
- O script lista produtos e cria pedidos com vários usuários simultâneos, versionado no repositório.
- O relatório mostra requisições por segundo, p95 e % de erro.
- Os mesmos números aparecem no Grafana durante o teste.

## Médio: caçar o gargalo
Colocar um problema de performance proposital e achar pelo APM.

**Validado quando:**
- Um problema é introduzido (ex.: N+1 no banco, ou uma chamada lenta) em uma branch.
- O problema é encontrado olhando **só** traces e métricas, sem ler o código, e o span culpado é apontado.
- Depois da correção, o teste de carga mostra a melhora em números (p95 antes × depois).

## Difícil: meta de performance
Definir e acompanhar um SLO.

**Validado quando:**
- Existe um SLO escrito (ex.: 99% dos `POST /api/pedidos` abaixo de 500 ms).
- Um painel mostra o SLO e o quanto do "orçamento de erro" já foi consumido.
- O teste de carga falha automaticamente (thresholds) quando o SLO é violado.
