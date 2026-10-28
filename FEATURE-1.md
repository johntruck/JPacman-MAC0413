# Feature 1 — Portões e interruptores

## Executar

- Jogo original: `./gradlew run`
- Mapa baseado no labirinto original, com os dois circuitos: `./gradlew run --args='/board-feature1.txt'`
- Mapa extra para testar fechamento pendente: `./gradlew run --args='/board-circuit-test.txt'`
- Testes automatizados: `./gradlew test`

O mapa original (`src/main/resources/board.txt`) foi mantido sem alterações. O mapa
`board-feature1.txt` preserva o formato de 23 × 21 casas, as paredes, os corredores, os pellets,
os fantasmas e a posição inicial do jogador do mapa original; acrescenta dois portões no corredor
superior e os interruptores perto do início.

O mapa `board-circuit-test.txt` é menor (19 × 11) para facilitar testes manuais. Ele tem dois
interruptores para cada circuito, dois portões para cada circuito e quatro fantasmas. Uma divisória
vertical separa o jogador dos fantasmas; os portões são as únicas passagens entre os lados.

## Legenda

- `1` e `2`: interruptores fixos dos circuitos 1 e 2. O jogador alterna o comando ao entrar.
- `a` e `b`: portões dos circuitos 1 e 2.
- Portão com chão: aberto; portão com parede: fechado; `~`: fechamento pendente.
- Fantasmas atravessam interruptores sem acioná-los.

## Como testar o fechamento pendente

1. Execute `./gradlew run --args='/board-circuit-test.txt'`.
2. Entre em um interruptor `1` ou `2` para abrir o circuito correspondente.
3. Observe a divisória central e espere um fantasma atravessar e ocupar um portão `a` ou `b` do circuito aberto.
4. Entre em outro interruptor com o mesmo número para comandar o fechamento.
5. Se ainda houver um ocupante no portão no momento do fechamento, ele deve mostrar `~`: não admite novas entradas,
   mas permite que o ocupante saia. Quando o último ocupante sair, o portão fecha. Acionar de novo
   o interruptor para abrir cancela o fechamento pendente.

O movimento dos fantasmas é dinâmico; pode ser necessário esperar ou repetir a tentativa para
conseguir que um fantasma ocupe um portão no momento do fechamento.

## Regras implementadas

Todos os portões começam fechados. Um circuito propaga o mesmo comando a seus portões, mas cada
portão lida individualmente com seus ocupantes: fica pendente quando recebe fechamento ocupado,
permite que os ocupantes saiam e fecha assim que o último sai. Abrir novamente cancela a pendência.
Mapas antigos sem novos símbolos continuam permitidos; circuitos incompletos e símbolos desconhecidos
são rejeitados com uma mensagem que identifica o problema.
