# 2. API Gateway

**Objetivo da etapa:** a tela e qualquer cliente falam com um endereço só; os serviços ficam "escondidos" atrás dele.

Ferramenta à escolha: Kong, Spring Cloud Gateway, Traefik, Nginx...

## Fácil: roteamento
Colocar um gateway no Compose com as rotas `/api/produtos` → `catalogo` e `/api/pedidos` → `pedidos`.

**Validado quando:**
- As duas APIs respondem pela porta do gateway.
- A tela usa só o endereço do gateway (as duas URLs viram uma).
- `catalogo` e `pedidos` não publicam mais porta para fora do Docker (só o gateway).

## Médio: proteção
Adicionar limite de requisições e um identificador por requisição.

**Validado quando:**
- Passando do limite (ex.: 10 req/s por cliente), o gateway responde `429 Too Many Requests` sem chegar no serviço.
- Toda requisição recebe um header de correlação (ex.: `X-Request-Id`) que aparece no log do serviço que a atendeu.
- O `@CrossOrigin` sai dos controllers e o CORS passa a ser responsabilidade do gateway.

## Difícil: resiliência
O gateway lida bem com um serviço fora do ar.

**Validado quando:**
- Com o `catalogo` parado, o gateway responde rápido com uma mensagem clara (timeout configurado), sem travar a tela.
- Com o `catalogo` parado, a rota de pedidos continua respondendo a listagem normalmente.
- Duas instâncias do `catalogo` rodando: o gateway distribui as requisições entre elas, e derrubar uma não derruba a rota.
