# 10. Kubernetes

**Objetivo da etapa:** o Mini Mercado roda num cluster, se recupera sozinho e escala com a carga.

Cluster local à escolha: Docker Desktop, kind, minikube ou k3d.

## Fácil: serviços no cluster
Levar `catalogo`, `pedidos` e o banco para o Kubernetes.

**Validado quando:**
- Cada serviço tem `Deployment` + `Service`, e as configurações vêm de `ConfigMap` e `Secret` (nenhuma senha no YAML do Deployment).
- O banco guarda os dados em volume persistente: apagar o pod do banco não perde dados.
- Criar um pedido funciona dentro do cluster.

## Médio: saúde e entrada
O cluster sabe quando um pod está pronto e o tráfego entra por um lugar só.

**Validado quando:**
- Os pods usam liveness e readiness (etapa 6) e têm `requests`/`limits` de CPU e memória.
- Matar um pod na mão: o Kubernetes cria outro e o sistema continua respondendo.
- O tráfego externo entra pelo gateway (ou um Ingress) e a tela funciona.
- Uma atualização de versão acontece sem tirar o sistema do ar (rolling update), e dá para voltar com `rollout undo`.

## Difícil: escala e observação
Escalar sozinho e continuar visível.

**Validado quando:**
- Um `HorizontalPodAutoscaler` aumenta as réplicas do `pedidos` durante o teste de carga (etapa 5) e diminui depois.
- Prometheus e Grafana (etapa 3) enxergam os pods do cluster.
- Os manifests ficam organizados para mais de um ambiente (ex.: Kustomize ou Helm), sem copiar e colar YAML.
