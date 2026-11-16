# 9. Terraform

**Objetivo da etapa:** a infraestrutura AWS das etapas 7 e 8 deixa de ser criada na mão e vira código.

## Fácil: recursos básicos
Criar com Terraform o bucket e as filas, apontando para o LocalStack.

**Validado quando:**
- `terraform plan` mostra o que será criado e `terraform apply` cria tudo no LocalStack.
- Os scripts manuais da etapa 7 podem ser removidos e o sistema continua funcionando.
- O `terraform.tfstate` não sobe para o git.

## Médio: a função também
Terraform cria a Lambda, as permissões e as ligações com as filas.

**Validado quando:**
- Lambda, gatilho da fila, DLQ e permissões (IAM com o mínimo necessário) são criados pelo Terraform.
- `terraform destroy` + `terraform apply` recriam o fluxo de pagamento inteiro e ele volta a funcionar.
- Nomes e valores que mudam por ambiente vêm de variáveis, e endereços úteis saem como `output`.

## Difícil: organização e ambientes
Código de infra reaproveitável.

**Validado quando:**
- Existe um módulo próprio (ex.: "fila com DLQ") usado mais de uma vez.
- Dá para criar dois ambientes separados (ex.: `dev` e `hml`) sem duplicar código, e eles não se misturam.
- Uma mudança pequena (ex.: tempo de retenção da fila) aparece no `plan` como alteração, e não como destruir e recriar.
