# 6. Monitoramento e Acesso

**Objetivo da etapa:** só quem pode faz o quê, e tudo que importa fica registrado.

## Fácil: health checks de verdade
Separar "está vivo" de "está pronto".

**Validado quando:**
- Cada serviço tem liveness e readiness separados.
- Com o banco desligado, o readiness do serviço fica `DOWN` e o liveness continua `UP`.
- Os healthchecks do Compose usam esses endpoints.

## Médio: login
Proteger a API com autenticação (ex.: Keycloak + JWT).

**Validado quando:**
- Listar produtos continua público; criar e cancelar pedido exigem usuário logado (sem token → `401`).
- O pedido passa a guardar o usuário do token, e não um nome digitado na tela.
- A tela faz login e manda o token nas chamadas.

## Difícil: permissões e auditoria
Papéis diferentes e rastro de quem fez o quê.

**Validado quando:**
- Existem pelo menos dois papéis: `CLIENTE` (compra e cancela só os próprios pedidos) e `ADMIN` (cadastra produtos, vê todos os pedidos). Sem permissão → `403`.
- Cancelar o pedido de outro cliente é bloqueado.
- Ações importantes (criar produto, cancelar pedido) geram um log de auditoria com usuário, ação e horário, visível no Grafana.
- As rotas internas de estoque (`baixa-estoque`, `repor-estoque`) não podem ser chamadas de fora pelo gateway.
