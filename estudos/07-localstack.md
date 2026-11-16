# 7. LocalStack

**Objetivo da etapa:** usar serviços da AWS sem conta e sem custo, rodando na máquina.

## Fácil: AWS local
Subir o LocalStack no Compose e criar recursos pela linha de comando.

**Validado quando:**
- O LocalStack sobe junto com o resto e o health mostra os serviços ativos.
- Um bucket S3 e uma fila SQS existem e são criados automaticamente quando o LocalStack sobe (script de inicialização).

## Médio: imagens dos produtos no S3
O catálogo passa a ter foto de produto guardada no S3.

**Validado quando:**
- Existe um endpoint para enviar a imagem de um produto, que salva no bucket.
- A tela mostra a imagem do produto.
- O código não tem endereço do LocalStack fixo: trocar para a AWS real seria só mudar configuração.

## Difícil: evento de pedido
O `pedidos` avisa o mundo quando um pedido é criado.

**Validado quando:**
- Ao criar um pedido, uma mensagem com os dados do pedido é publicada numa fila SQS.
- Se o LocalStack estiver fora, o pedido **não** fica num estado inconsistente (decisão documentada: falha o pedido ou guarda para reenviar).
- Um teste automatizado sobe o LocalStack (ex.: Testcontainers) e verifica que a mensagem chegou na fila.
