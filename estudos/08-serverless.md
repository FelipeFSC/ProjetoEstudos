# 8. Desenvolvimento Serverless

**Objetivo da etapa:** o pagamento vira uma função que roda sob demanda, fora dos serviços.

## Fácil: primeira Lambda
Criar uma função de pagamento e publicar no LocalStack.

**Validado quando:**
- A função é publicada e, chamada diretamente (`invoke`), devolve `APROVADO` ou `RECUSADO` (ex.: recusa acima de um valor).
- O código fica em uma pasta própria no repositório, com build que gera o pacote da função.

## Médio: pagamento assíncrono
Ligar a Lambda ao fluxo do pedido pela fila.

**Validado quando:**
- O pedido nasce como `AGUARDANDO_PAGAMENTO`.
- A fila da etapa 7 aciona a Lambda automaticamente.
- A Lambda publica o resultado em outra fila, o `pedidos` consome e o pedido vai para `PAGO` ou `RECUSADO`.
- A tela mostra o status mudando sem recarregar o pedido na mão (pode ser atualização periódica).

## Difícil: falhas
Nada se perde e nada é cobrado duas vezes.

**Validado quando:**
- Mensagens que falham várias vezes vão para uma DLQ (fila de mensagens mortas).
- Processar a mesma mensagem duas vezes não muda o resultado (idempotência), comprovado por teste.
- Pedido `RECUSADO` devolve o estoque ao catálogo.
- A execução da Lambda aparece no mesmo trace do pedido (etapa 4).
