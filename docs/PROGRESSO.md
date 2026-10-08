# Progresso — 04/10/2026

## Logo integrada ao app — 07/10/2026

Implementados ícone adaptativo MD (inclusive monocromático) e capa de abertura com a logo completa por até 1,8 s, dispensável por toque ou Voltar. Os recursos antigos de launcher foram substituídos; fluxo do menu preservado. Build debug, lint e 17 testes unitários concluídos sem falhas; lint 0 erros e 9 avisos. APK em app/build/outputs/apk/debug/app-debug.apk. Não houve inspeção visual em aparelho: ADB consultado com permissão e não listou dispositivos.

## Revisão de layout e espaçamento — 07/10/2026

Implementados: cartões de melhorias com ícone à esquerda e conteúdo à direita; padding/margens explícitos em retrato, alvo, estatísticas, preços e ações; HUD com altura flexível e rodada em segunda linha quando faltar largura; emblemas centralizados em cartões quadrados de 72 dp; painel azul escuro; separação entre confirmar/cancelar. Alvos de toque mínimos de 48 dp e textos reais preservados. Painel esquerdo continua sobreposto/rolável, sem modificar a geometria do cenário. Arquivos: screen_battle.xml, styles.xml, combat_panel.xml e MainActivity.java.

Verificado: primeiro build debug/lint e 17 testes existentes passaram (16 do motor e 1 teste original), sem falhas/erros. Build final após ajustes do HUD passou em 1 min 13 s; lint 0 erros e 8 avisos, sem avisos no layout de combate. APK atualizado em app/build/outputs/apk/debug/app-debug.apk. PNG do mapa confirmado com SHA-256 original. Pendente: instalação/capturas reais da versão final, painel aberto/fechado, seleção, saldo insuficiente, textos longos e outros formatos. Aparelho inicialmente conectado, mas ausente nas consultas posteriores; nenhuma AVD utilizável listada. Não declarar validação visual com base nas capturas antigas.

## Estilo das referências — 06/10/2026

Implementados emblemas vetoriais nos botões compactos da equipe, HUD com coração/moeda próprios, tipografia pesada e melhorias com ícones de energia/alcance. Barra direita reduzida de 136 para 112 dp; esquerda de 184 para 176 dp. Custos/estados acessíveis em tooltip e descrição. Cenário continua como fundo e ações existentes preservadas. Resultado de build/lint e inspeção em VALIDACAO.md.

## Fundo da batalha — 06/10/2026

Aplicado pedido de eliminar mapa flutuando na moldura azul: FrameLayout com fundo de batalha preenchendo área disponível, HUD/legenda sobrepostos e melhorias sobre a esquerda. Mesma arte, escala uniforme cover, recorte periférico conforme proporção; preview/preparação/menu preservados. Azul somente nos painéis/controles. Build debug e lint concluídos: BUILD SUCCESSFUL em 1 min 23 s, 0 erros/7 avisos. Nenhum teste automatizado criado/executado. APK instalado (Success); resultado visual/manual detalhado em VALIDACAO.md.

## Interface de combate — 05/10/2026

Implementado layout azul/ciano com HUD próprio, coluna direita de retratos/estados/custos e coluna esquerda de melhorias clara/dourada, retrato maior e venda vermelha. Mapa completo recalculado ao abrir/fechar painel; sem recorte/esticar/alterar geometria. Legenda fora do mapa e acabamento decorativo azul nas margens não jogáveis. Ações reais de onda/pausa/2x/saída/compra/venda/ficha preservadas, com alvos de 48 dp. Menu/pop-up/ambiente/portal preservados. Build/lint e evidência manual em VALIDACAO.md; nenhum teste automatizado criado ou executado.

## Ambiente — 05/10/2026

Implementados VisualClock, EnvironmentScene, EnvironmentRenderer e CampaignImageView. Integração em CampaignPager, BattlefieldView e MainActivity: suspensão atrás da preparação e no ciclo da Activity; atualização apenas das páginas visíveis. Água mascarada na torre/base/combate; nuvens periféricas de baixa opacidade; dois sprites de veleiros na torre com fundo derivado sem barcos estáticos. Trajetos locais contínuos de 40 s. Aeroporto sem água. Pop-up, controles, enquadramento, motor e Comic Vine preservados.

Recursos derivados inspecionados como imagens estáticas. Build/lint e disponibilidade de aparelho em VALIDACAO.md. Nenhum teste automatizado criado/executado. Pendentes no aparelho: ciclo completo, alinhamento em redimensionamento, gestos/encaixe, preparação/equipe/combate e suspensão/retomada observada.

## Inspeção concluída
Projeto inicial Java/AppCompat, tela Hello World. Gradle/AGP/SDK preservados. SDK local instalado; adb disponível, nenhum aparelho conectado no início. Não existe repositório Git inicializado nesta pasta.

## Implementação concluída da base solicitada
1. Documentação e arquitetura registradas.
2. Menu, escolha de equipe e entrada/saída da missão.
3. Mapa próprio em Canvas com curvas, obstáculos, portal e base.
4. Colocação livre com prévia de alcance, validade, confirmação/cancelamento e cobrança única.
5. Motor Java puro: passo fixo, movimento no caminho, pausa e propriedade de estado na thread principal.
6. Combate automático com alvo prioritário, área e desaceleração; créditos e dano à base.
7. Cinco ondas, pausa, velocidade 1x/2x, vitória/derrota e reinício.
8. Painel de dano/alcance e venda; limite de evolução por unidade.
9. Comic Vine: seis perfis reais, cache offline, ficha/atribuição e atualização em background.

Etapas do documento original 0–7 implementadas como uma única base autorizada. Integração 8 tem catálogo/poderes funcionando no host e cliente Android compilado; execução do refresh em aparelho ainda não verificada. Revisão 9 teve build, testes e lint; percurso visual ainda pendente.

## Evidências
- Seis consultas Comic Vine reais com status_code 1, IDs e publisher Marvel verificados; poderes reais salvos em assets/comicvine_profiles.json. Chave em local.properties, sem exposição em evidências.
- APK debug gerado em app/build/outputs/apk/debug/app-debug.apk.
- Gradle: build debug, testes e lint concluídos com BUILD SUCCESSFUL.
- Testes: 16 verificações do motor + 1 teste original, zero falhas/erros. Relatórios em app/build/test-results/testDebugUnitTest.
- Lint: zero erros; avisos de orientação e otimizações de layout, detalhados em VALIDACAO.md.
- Erro inicial da JVM Windows foi contornado na execução via fallback TCP; versões do projeto mantidas. Ajuste documentado no README.
- Capturas oficiais de Bloons TD foram vistas no navegador; não são capturas deste aplicativo.

## Próxima ação
Executar no Android Studio com emulador/aparelho, conferir o roteiro de VALIDACAO.md e enviar os ajustes de arte/modelagem desejados. A base está implementada com validação de build/lógica; a validação visual e de ciclo Android permanece pendente. Não há salvamento de partida após recriação/processo.

## Atualização solicitada — novo mapa da Torre dos Vingadores
- Substituído o terreno gerado em Canvas pelo PNG enviado pelo usuário, em drawable-nodpi/manhattan_map.png. Original copiado sem alteração; SHA-256 idêntico ao anexo.
- Cenário completo em menu, equipe e partida, com proporção 1672:941 e escala uniforme. Desenho e toque continuam usando o mesmo viewport.
- Caminho de 217 pontos traçado sobre a rua, do portal de Loki à porta da torre; comprimento lógico aproximado 1270,37.
- Bloqueios: círculos para árvores e polígonos para água, prédios, torre, portal, veículos, ilha central e píer; considera o raio ocupado do herói nas bordas.
- Nome e textos da missão atualizados. Removidos desenho e legendas do mapa anterior.
- Cenários existentes do motor passaram a usar uma geometria sintética explícita, preservando coordenadas, tempos e critérios anteriores. Não são evidência de integração do novo mapa; não foram reexecutados nesta alteração.
- Exportação auxiliar da geometria em build/map-preview/index.html, usando MapData/GameEngine reais, encontrou 351 posições livres na grade de 15 unidades. Isso não substitui conferência visual/toque no Android.
- Imagem copiada inspecionada; nenhuma captura da execução Android foi produzida. Aparelho/emulador continuam indisponíveis.

Compilação após a troca do mapa: gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon --console=plain, com fallback JVM do README. BUILD SUCCESSFUL em 54 s. APK atualizado; lint 0 erros e 7 avisos em recursos existentes: orientação, layouts e ícones launcher. Sem execução de testes nesta atualização.



## Atualização solicitada — giro do portal
- Sobreposição Canvas em PortalRenderer: abertura elíptica ajustada à imagem, espiral verde girando, núcleo pulsante e pontos de energia.
- Volta completa em seis segundos; moldura e chifres fixos. PNG original preservado.
- BattlefieldView anima a camada também em menu/equipe, respeita visibilidade/foco da janela e cancela o animator ao trocar de tela. Animação decorativa independente da simulação, pausa de combate e velocidade 2x.
- Conferência visual do alinhamento, giro e transições no Android ainda pendente; nenhum teste foi adicionado ou executado nesta alteração.

Build do portal: :app:assembleDebug :app:lintDebug --no-daemon --console=plain (fallback JVM do README), BUILD SUCCESSFUL em 2 min 19 s. APK atualizado; lint 0 erros e 7 avisos. Resultado visual no Android continua pendente.


## Menu principal de seleção de regiões — implementação
- Substituído screen_menu.xml por screen_campaign_menu.xml. CampaignPager usa três páginas Android nativas, rolagem horizontal, encaixe no índice mais próximo, cancelamento seguro de encaixe anterior e recálculo de largura em onMeasure.
- Entrada na Torre dos Vingadores; somente essa região habilita Preparar missão e abre showTeam existente. Base destruída e Aeroporto são prévias bloqueadas com Em breve; sem lógica de desbloqueio ou novas fases.
- Imagens principais FIT_CENTER e completas; pistas laterais desbotadas de 16 dp. Rodapé Android com controles de 48 dp e indicador discreto de região; sem repetir nomes/cadeados da arte. Regiões bloqueadas recebem filtro de cor visual.
- Recursos finais salvos no projeto. Placa da primeira imagem corrigida para TORRE DOS VINGADORES, conforme resposta do usuário, via image_gen integrada; detalhes e prompt em REFERENCIAS.md. Outros dois PNGs iguais aos anexos, hashes conferidos.
- MainActivity mudou somente a construção do menu. Seleção de equipe, motor, mapa de combate, portal animado e Comic Vine preservados. SHA-256 de manhattan_map.png continua 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2.
- Sem alteração de fontes de testes, sem testes automatizados adicionados ou executados.
- Aparelho modelo 24095PCADG, API 36, disponível intermitentemente. APK instalado e aplicativo lançado. A inspeção inicial revelou páginas com largura de 1 px, corrigidas transferindo o dimensionamento de onSizeChanged para onMeasure; a conferência visual após a correção está pendente: conexão intermitente e outra janela sobre o jogo impediram observar a arte e enviar gestos com segurança.


Build final do menu após correção de medição: :app:assembleDebug :app:lintDebug --no-daemon --console=plain, fallback JVM do README. BUILD SUCCESSFUL em 2 min 19 s; lint 0 erros e 7 avisos em recursos existentes. APK final instalado no aparelho e MainActivity lançada (Status: ok). Nenhum teste automatizado executado. Rolagem, encaixe, proporção e navegação ainda precisam de conferência visual sem outra janela cobrindo o jogo.


## Ajuste solicitado — mapa preenchendo a tela
O usuário pediu eliminar o efeito de mapa flutuando no centro. Menu agora ocupa a tela inteira, com escala uniforme para cobrir a área, recorte do excedente superior e placa inferior preservada. Controles sobrepostos; barras do sistema ocultas apenas no menu. PNGs aprovados e mapa de combate inalterados.

Na conferência manual da versão anterior, torre/base/aeroporto, gestos curtos/longos, encaixe, indicador, bloqueio de partida e retorno pelas setas funcionaram. Um arrasto inteiramente dentro de Preparar missão abriu a equipe indevidamente. Corrigido com SwipeSafeButton: arrasto maior que touchSlop cancela a pressão sem clique.

Arquivos adicionais/ajustados: ui/CampaignPager.java, ui/SwipeSafeButton.java, MainActivity.java, res/layout/screen_campaign_menu.xml, README.md e docs/PROJETO.md, PROGRESSO.md, VALIDACAO.md. Build e conferência da versão em tela cheia registrados abaixo. Nenhum teste automatizado adicionado ou executado nesta etapa.

### Resultado final — tela cheia, 04/10/2026
- Build final: :app:assembleDebug :app:lintDebug --no-daemon --console=plain, fallback JVM do README. BUILD SUCCESSFUL em 2 min 33 s na segunda tentativa; lint então apontou 0 erros e 8 avisos. A chamada explícita a performClick foi ajustada na revisão final, cujo resultado está registrado abaixo. Erro AppCompatCustomView e aviso ClickableViewAccessibility da primeira tentativa corrigidos usando AppCompatImageView e performClick nativo; nenhuma regra suprimida.
- Conferência manual no aparelho 24095PCADG, API 36, tela 2712 x 1220: três regiões preenchem a tela, sem margens vazias e sem esticar; placas inferiores e cadeados das prévias visíveis. Para cobrir esta proporção, parte superior das artes fica recortada, incluindo o topo da torre e da torre de controle do aeroporto. Esta escolha substitui o requisito anterior de exibir toda a imagem, conforme pedido posterior de tela cheia.
- Gesto curto retornou a 1/3; gesto longo encaixou em 2/3 e depois 3/3. Setas retornaram à torre. Em breve não abriu equipe; Preparar missão apareceu somente em 1/3. Arrasto dentro do botão não abriu equipe após correção; toque normal abriu a equipe existente e Voltar retornou à torre.
- Barras do sistema ocultas no menu e restauradas na equipe. A seleção de equipe exibiu o cenário de combate existente e Comic Vine/dados salvos. Motor, portal e PNG de combate preservados em código; giro completo, partida e atualização de API não foram revalidados nesta conferência.
- Evidências visuais: docs/evidencias/campaign-fullscreen.png, campaign-fullscreen-short.png, campaign-fullscreen-base.png, campaign-fullscreen-airport.png e campaign-fullscreen-team.png. Capturas feitas durante inspeção manual via adb; nenhum teste automatizado criado ou executado.
- Pendentes: TalkBack, teclado, redimensionamento, outros formatos de tela e ciclo de segundo plano. Os registros anteriores de impedimento por outra janela são históricos; o usuário fechou a janela e permitiu esta conferência.
- APK: app/build/outputs/apk/debug/app-debug.apk. Documentação de arquitetura atualizada em PROJETO.md.

Revisão final do botão: build e lint BUILD SUCCESSFUL em 3 min 33 s; 0 erros e 7 avisos existentes, sem novos avisos. APK final instalado (Success), MainActivity aberta (Status: ok). Arrasto e toque normal novamente conferidos no APK final; evidências campaign-fullscreen-final.png e campaign-final-team.png em docs/evidencias. Jogo deixado no menu da torre. Nenhum teste automatizado executado.


## Preparação sobre a campanha — 04/10/2026
Pedido: remover Preparar missão do canto da tela, usar a plataforma verde como entrada e abrir um pop-up com temática dos Vingadores, inspirado na composição da referência e sem baús.

Implementação: CampaignPager mantém um botão Android transparente com ripple sobre o círculo verde já presente na arte. Posição e tamanho seguem a matriz da imagem da torre; área mínima de toque 48 dp. Gesto de arrasto cancela clique e o pop-up só abre com a região liberada encaixada. As prévias continuam bloqueadas.

popup_mission.xml é uma sobreposição na mesma Activity: mapa da campanha permanece visível e escurecido atrás. Moldura azul/dourada, título Central dos Vingadores, prévia do mapa de combate com portal, dificuldade Normal (cinco ondas), seis heróis em grade, até três slots selecionados, Poderes/fichas, atualização Comic Vine e Entrar na missão. Sem baús, recompensas, dificuldades novas ou desbloqueios. Arte dos personagens e balanceamento permanecem provisórios.

Fechar, toque fora do painel e Voltar removem apenas o pop-up e mantêm a campanha; seleção da equipe permanece na sessão. Ao entrar, usa o mesmo GameEngine/MapData/BattlefieldView. Fichas e fila/cache/intervalo Comic Vine preservados; callback de atualização consulta se o pop-up ainda existe. Conteúdo do mapa atrás fica fora da árvore de acessibilidade enquanto o pop-up está aberto.

Arquivos: MainActivity.java; ui/CampaignPager.java; res/layout/screen_campaign_menu.xml e popup_mission.xml; res/drawable/mission_panel.xml e mission_platform_touch.xml; res/values/strings.xml. Nenhum teste automatizado adicionado ou executado. Resultados de build/lint e inspeção manual registrados em VALIDACAO.md.

A revisão final mantém o alvo de toque como botão nativo sobreposto, medido normalmente e posicionado pela mesma matriz da arte. Durante o gesto da campanha ele fica oculto; SwipeSafeButton cancela arrastos sobre o próprio alvo. screen_team.xml removido após substituição por popup_mission.xml; textos antigos sem uso removidos. SwipeSafeButton.java também ajustado nesta etapa.


Build final da preparação: BUILD SUCCESSFUL em 2 min 15 s; lint 0 erros e 8 avisos, detalhados em VALIDACAO.md. APK instalado. Pop-up, limite de três, slots, fechar, equipe vazia e cancelamento de arrasto observados no aparelho. Entrada no combate/ficha individual aguardam conferência com estado estável. Nenhum teste automatizado executado.


Ficha individual do Homem-Aranha conferida no pop-up; poderes Comic Vine e atribuição presentes. Entrada no combate continua pendente de conferência com equipe estável.


## Novo visual do pop-up — referência UX/UI de 04/10/2026
Aplicado o layout do anexo codex-clipboard-55c5b3bc-efde-403a-80d2-ae5bf12b1550.png: painel claro com borda/sombra azul, faixa azul com título branco, seções azul-claro com contorno tracejado, prévia à esquerda, seis cartões em duas linhas à direita e indicador de equipe no canto da seção. Cartões têm emblemas simples, nomes e ação +/✓; seleção também muda o contorno para verde. O estado real da equipe é preservado, sem zerar somente para igualar o exemplo 0/3 da referência.

Rodapé: Normal/cinco ondas, Poderes/fichas amarelo, Atualizar fichas azul e Entrar na missão verde. Status Comic Vine abaixo das ações. Botão de fechar explícito; mapa da campanha escurecido e desfocado usando RenderEffect (minSdk existente 33), restaurado ao fechar. Controles Android reais; emblemas e fundos feitos com TextViews/drawables, sem usar o mockup como imagem de interface. A prévia mantém o mapa de combate aprovado, sem substituir pela variante do mockup.

Não alterados: limite de três heróis, catálogo/balanceamento, Comic Vine, motor, mapa de combate e animação do portal. Sem baús. Removida a faixa adicional de nomes selecionados, pois +/✓ e contorno nos cartões já representam a equipe como na referência. Tamanho mínimo das ações dos cartões 48 dp; o cartão inteiro também alterna seleção.

Arquivos: MainActivity.java, res/layout/popup_mission.xml, res/values/strings.xml, res/drawable/mission_panel.xml; novos mission_section.xml, mission_card.xml, mission_badge.xml, mission_add.xml, mission_ribbon.xml e mission_button_yellow/blue/green.xml. Resultados de compilação/lint e inspeção registrados em VALIDACAO.md. Nenhum teste automatizado adicionado ou executado.

## Logo provisória — 07/10/2026
Gerada proposta raster com fundo transparente em art/marvel-defence-logo.png. Arquivo salvo para avaliação; ainda não integrado à interface nem verificado em aparelho.


## Ícone mobile provisório — 07/10/2026
Gerada proposta quadrada 1254 x 1254 com monograma MD em art/marvel-defence-app-icon.png, derivada visualmente da logo anterior. Arquivo salvo para avaliação; launcher Android ainda não substituído. Inspeção visual da imagem gerada realizada; não verificado em aparelho.


## Card visual de divulgação — 07/10/2026
Gerado e salvo em art/card-divulgacao-marvel-defence.png. Composição promocional horizontal mostra portal, caminho em S, onda de drones com barras de vida, três defesas posicionadas com alcance e ataques e base no destino. É arte promocional baseada na logo e no mapa do projeto, não captura de gameplay. Inspeção visual da imagem gerada realizada; não integrada ao APK.


## Card revisado com captura real — 07/10/2026
Após correção do usuário, o card final é art/card-divulgacao-marvel-defence-captura.png (1280 x 720). Usa a captura do combate fornecida pelo usuário em 07/10, com logo do projeto e legenda promocional. Não há torretas nem unidades inventadas; os personagens, drones e mapa são da própria captura. Fonte editável em art/card-divulgacao-marvel-defence-original.svg. O card anterior permanece apenas como proposta descartada.


## Card promocional com heróis — 07/10/2026
A pedido do usuário, edição do primeiro card ilustrado preservando composição, logo, caminho, ondas e cores, substituindo as três torretas por personagens pequenos em pé inspirados nos sprites provisórios da captura de combate. Final em art/card-divulgacao-marvel-defence-herois.png. É ilustração promocional, não captura real nem modelo final dos personagens. Inspecionado visualmente; não integrado ao APK.

