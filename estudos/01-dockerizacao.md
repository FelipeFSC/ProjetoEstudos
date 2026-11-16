# 1. Dockerização

**Objetivo da etapa:** o Mini Mercado inteiro sobe com um único comando.

## Fácil: imagem do catálogo
Criar um `Dockerfile` para o serviço `catalogo`.

**Validado quando:**
- `docker build` gera a imagem sem erro.
- O container sobe, conecta no `minimercado-db` e `GET /api/produtos` responde pela porta publicada.
- A imagem final não contém o Maven nem o código-fonte (só o necessário para rodar).

## Médio: tudo no Compose
Criar um `compose.yml` na raiz com `catalogo`, `pedidos` e o PostgreSQL.

**Validado quando:**
- `docker compose up -d --build` sobe os três, sem nenhum passo manual (os bancos `catalogo` e `pedidos` são criados sozinhos).
- O `pedidos` fala com o `catalogo` pelo nome do serviço na rede do Compose (variável `CATALOGO_URL`), não por `localhost`.
- Os dados continuam lá depois de `docker compose down` + `up` (volume).
- Criar um pedido pela API funciona com tudo em containers.

## Difícil: pronto para produção
Deixar o Compose robusto e incluir a tela.

**Validado quando:**
- A tela (`web/`) é servida por um container (ex.: Nginx) e funciona no navegador.
- Os serviços só sobem depois que o banco está pronto (`healthcheck` + `depends_on: condition`).
- Os containers Java não rodam como `root`.
- Senhas saem do `compose.yml` e vão para um arquivo `.env` que não sobe para o git.
- Derrubar o container do `pedidos` e subir de novo não perde nenhum pedido.
