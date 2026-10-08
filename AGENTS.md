# Marvel Defence

## Escopo
Base Android nativa em Java, 2D vista de cima, horizontal, inspirada em Bloons TD e nas sete imagens anexadas. Em 04/10/2026 o usuário autorizou construir a base jogável nesta pasta; ele irá modelar os personagens e ajustar as regras depois. O documento de prompts é referência de etapas.

## Protocolo
Antes de alterar, ler docs/PROJETO.md, docs/PROGRESSO.md e docs/VALIDACAO.md e inspecionar o código relevante. Preservar trabalho existente. Executar somente o escopo solicitado; implementar, verificar, corrigir e atualizar progresso. Diferenciar implementado de verificado. Resolver escolhas técnicas rotineiras autonomamente.

## Implementação
- Java; menus e controles Android reais; Canvas para mapa; motor sem imports Android.
- Identidade/poderes Comic Vine independentes de balanceamento e arte dos heróis.
- Posicionamento livre; geometria consistente para desenho, toque e validação.
- Configurações provisórias centralizadas; evitar abstrações sem necessidade.
- Estado mutável na thread principal; parar atualização em segundo plano sem compensar tempo ausente.
- API fora do combate. Não inventar campos/endpoints. Consultar fontes oficiais para dúvidas.
- Não adicionar login, loja, ranking, multiplayer ou monetização.

## Credenciais
O usuário autorizou usar a chave Comic Vine no protótipo local. Configurá-la em local.properties (ignorado); nunca repetir em logs, documentação ou arquivos versionados. Build debug pode incorporá-la conforme autorização. Release não deve incorporar a chave local.

## Verificação
Executar build debug e verificações significativas do motor. Nunca declarar teste sem execução nem enfraquecer critérios para esconder falhas. Se faltar emulador/aparelho, registrar o bloqueio e passos exatos no Android Studio. Não declarar fidelidade visual sem inspeção real. Documentar origem da arte provisória.
