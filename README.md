# Backend do sistema de pedidos

## Base usada

O servidor foi montado a partir do exemplo Java multithreaded da Aula 6. Foram mantidos o `ServerSocket`, o ciclo de `accept()` e a criacao de uma thread por conexao. O cliente de console usa uma thread receptora, seguindo o exemplo de cliente multithreaded.

## Adaptacoes comentadas no codigo

- `TCPServerAtivosHandler`: substitui o repasse generico de mensagens pelo despacho dos comandos de negocio.
- `PedidoService`: implementa catalogo, validacao e baixa sincronizada de estoque, registro em memoria e fila FIFO com `wait()`/`notifyAll()`.
- `TCPServerConnection`: adiciona escrita sincronizada para nao misturar mensagens simultaneas no mesmo socket.
- `TCPClientConsole`: permite exercitar a comunicacao sem depender da interface grafica.

## Protocolo de texto

Cada mensagem termina com nova linha; os campos usam `|` como separador. O cliente envia `CONECTAR|nome` antes dos comandos.

| Comando | Exemplo |
| --- | --- |
| Catalogo | `CATALOGO` |
| Criar pedido | `PEDIR|0|1|BANANA` |
| Consultar status | `STATUS|1` |
| Encerrar conexao | `SAIR` |

Opcionais aceitos: `NENHUM`, `BANANA`, `BIS` e `BANANA+BIS`. O catalogo e o estoque sao carregados em memoria ao iniciar o servidor. Cada pedido contem um produto; pedidos com mais produtos podem ser enviados como pedidos separados.

Respostas principais: `CONECTADO|nome`, `CATALOGO_ITEM|id|nome|preco|disponivel`, `FIM_CATALOGO`, `PEDIDO_CRIADO|id|status|previsaoSegundos`, `STATUS_PEDIDO|id|status|previsaoSegundos`, `EVENTO_STATUS|id|status` e `ERRO|codigo|mensagem`. Quando um pedido aceito altera a disponibilidade, o servidor envia `CATALOGO_ATUALIZADO`, seguido dos itens atuais e `FIM_CATALOGO`, a todos os clientes conectados.

## Execucao local

Compile os arquivos Java da pasta `src` a partir da raiz do projeto. Inicie `multithreads2.TCPServerAtivosMain` e depois execute uma ou mais instancias de `multithreads2.TCPClientConsole`. O cliente usa `127.0.0.1:5000` por padrao; endereco, porta e nome podem ser passados como argumentos.

## Premissas iniciais a confirmar com o grupo

- Um pedido representa um produto e uma quantidade; nao ha carrinho agregado nesta primeira versao.
- Cada unidade de opcional consome uma banana ou um Bis, independentemente do tamanho do produto.
- Os ingredientes sao reservados ao aceitar o pedido; a fila processa na ordem de chegada.
- Os tempos de producao sao simulados com 3 segundos para produtos de 200 ml, 5 segundos para 400 ml e 0 para agua.
- Os dados ficam somente na memoria e voltam aos valores iniciais quando o servidor reinicia.
