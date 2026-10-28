# Feature 1 — Portões e interruptores

## Executar

- Jogo original: \`./gradlew run\`
- Mapa de demonstração: \`./gradlew run --args='/board-feature1.txt'\`
- Testes: \`./gradlew test\`

O mapa original (\`src/main/resources/board.txt\`) foi mantido. O mapa de demonstração fica em
\`src/main/resources/board-feature1.txt\`.

## Legenda

- \`1\` e \`2\`: interruptores dos circuitos 1 e 2; o jogador alterna o comando do circuito ao entrar.
- \`a\` e \`b\`: portões dos circuitos 1 e 2.
- Portão com chão: aberto; portão com parede: fechado; \`~\`: fechamento pendente.
- Fantasmas atravessam interruptores sem acioná-los.

## Regras implementadas

Todos os portões começam fechados. Um circuito propaga o mesmo comando a seus portões, mas cada
portão lida individualmente com seus ocupantes: fica pendente quando recebe fechamento ocupado,
permite que os ocupantes saiam e fecha assim que o último sai. Abrir novamente cancela a pendência.
Mapas antigos sem novos símbolos continuam permitidos; circuitos incompletos e símbolos desconhecidos
são rejeitados com uma mensagem que identifica o problema.
