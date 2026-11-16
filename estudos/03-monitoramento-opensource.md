# 3. Monitoramento Open Source

**Objetivo da etapa:** ver a saúde e o volume do sistema em gráficos, com ferramentas livres.

## Fácil: métricas expostas
Expor métricas dos dois serviços no formato do Prometheus.

**Validado quando:**
- Os dois serviços têm endpoint de health e de métricas (ex.: Spring Boot Actuator + Micrometer).
- Prometheus sobe no Compose e mostra os dois alvos como `UP`.

## Médio: dashboard
Subir o Grafana e montar um painel do Mini Mercado.

**Validado quando:**
- O Grafana já sobe com o Prometheus configurado como fonte de dados (provisionado por arquivo, sem clicar).
- O painel mostra, por serviço: requisições por segundo, % de erro e latência p95.
- Existe pelo menos uma métrica de **negócio** criada no código (ex.: pedidos criados, pedidos cancelados) aparecendo no painel.

## Difícil: alerta e logs
Ser avisado quando algo dá errado e ver os logs no mesmo lugar.

**Validado quando:**
- Uma regra de alerta dispara quando a taxa de erro passa de um limite (dá para forçar o erro e ver o alerta mudar de estado).
- Os logs dos containers chegam no Grafana (ex.: Loki) e dá para filtrar por serviço e por nível (`ERROR`).
- O painel e as regras ficam versionados em arquivos no repositório.
