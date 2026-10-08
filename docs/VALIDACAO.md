# Validação

## Logo, capa e ícone — 07/10/2026

Executado `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug --offline --no-daemon --console=plain` com o fallback JVM documentado: BUILD SUCCESSFUL. Relatórios JUnit: 16 testes GameEngineTest e 1 ExampleUnitTest, zero falhas/erros. Lint: 0 erros e 9 avisos. O build confirma recursos e código compiláveis; os testes do motor não verificam aparência da capa ou do ícone. APK debug gerado em app/build/outputs/apk/debug/app-debug.apk.

ADB `devices -l` consultado com permissão fora do sandbox: lista vazia. Inspeção visual pendente. No Android Studio: conectar aparelho API 33+ ou iniciar AVD no Device Manager; executar `app`; conferir monograma MD no launcher (ícone normal e temático, se disponível), abrir o app e conferir a logo completa centralizada na capa em horizontal; verificar que toque/Voltar a dispensam e que ela desaparece automaticamente em cerca de 1,8 s antes do menu da Torre. Conferir também que ao voltar da missão a capa não reaparece.

## Revisão de layout e espaçamento — 07/10/2026

Documentação, layout, estilos, combat_* e criação/atualização dos botões em MainActivity lidos antes de alterar. Os cinco anexos foram usados como referência visual. Nenhum mockup foi colado na interface; os emblemas e retratos provisórios continuam recursos locais já existentes. Mudanças em screen_battle.xml, styles.xml, combat_panel.xml e MainActivity.java; nenhum teste novo criado.

Primeira execução: gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon --console=plain, com fallback JVM do README. BUILD SUCCESSFUL em 2 min 5 s. Relatórios JUnit confirmam 16 testes GameEngineTest e 1 ExampleUnitTest, zero falhas/erros e nenhum ignorado. Esses testes verificam o motor; não validam padding nem layout Android. A versão final acrescentou ajuste responsivo do HUD, grupos de contadores e margens do retrato/posicionamento.

Verificação final: gradlew.bat :app:assembleDebug :app:lintDebug --offline --no-daemon --console=plain, mesmo fallback JVM. BUILD SUCCESSFUL em 1 min 13 s. Lint: 0 erros e 8 avisos (orientação fixa, merge da raiz, dois pesos no pop-up, string unit_stats sem uso, pai do pop-up e duas variantes launcher); nenhum aviso em screen_battle.xml. Sem suprimir regras. APK final em app/build/outputs/apk/debug/app-debug.apk. Testes do motor não foram repetidos após as alterações finais da interface.

Inspeção estática: largura do mapa e Viewport.cover não dependem da abertura do painel; somente HUD e legenda recebem margem esquerda de 176 dp. Cards dos heróis 72 × 72 dp com emblema 40 dp e padding horizontal 16 dp; botões de ação têm altura 48 dp; chips HUD não usam mais altura fixa 30 dp. Títulos/níveis/estatísticas podem quebrar linha; não há redução automática de fonte nem ellipsis. Painel esquerdo permite rolar o conteúdo em alturas pequenas. Estes itens foram conferidos no código, sem afirmar resultado visual.

PNG manhattan_map.png: SHA-256 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2, igual ao original registrado. API, configurações de balanceamento, motor, cenário e renderizadores não foram modificados.

Bloqueio de capturas: adb devices -l inicialmente mostrou 24095PCADG; após a compilação as duas consultas com o ADB do SDK retornaram lista vazia. emulator -list-avds não retornou AVD; a pasta Medium_Phone.avd existente está incompleta e não foi alterada. emulator -accel-check informou código 6 e necessidade de habilitar Hyper-V/Windows Hypervisor Platform. Nenhuma captura da versão final foi gerada ou inspecionada nesta revisão. Instalação e fidelidade visual final permanecem pendentes.

### Roteiro exato de inspeção pendente

1. Android Studio: selecionar aparelho Android API 33+ com depuração USB autorizada e clicar Run app; alternativamente Device Manager → Create Virtual Device → imagem API 33+ → Finish → iniciar a AVD, com virtualização configurada.
2. Torre → plataforma verde → selecionar Homem de Ferro, Thor e Hulk → Entrar na missão. Capturar painel fechado: conferir HUD, margem entre ícones/texto/bordas, barra azul escura, cards quadrados e botões inferiores.
3. Selecionar Homem de Ferro; capturar seleção verde e confirmar/cancelar separados; tocar área livre próxima à rua e confirmar. Tocar a unidade: capturar painel aberto, alcance alinhado, cenário imóvel e HUD/legenda fora do painel.
4. Comprar dois níveis de dano: preços reais passam por $120, $220 e $320, e o texto de nível inclui +40% base; conferir também o preço de venda atualizado. Capturar e verificar quebra de linha, alinhamento e respiro interno; rolar o painel para conferir venda/ficha se necessário.
5. Com essa equipe e compras, restam $340: Thor ($380) e Hulk ($360) devem aparecer indisponíveis. Capturar e verificar estado cinza, impedir colocação e conferir que a unidade já colocada continua selecionável. Conferir também melhoria sem saldo e nível máximo em uma partida com créditos suficientes.
6. Fechar painel e comparar enquadramento/posição das unidades. Conferir iniciar onda, pausar/retomar, 1x/2x, venda e retorno. Repetir em outra proporção e com fonte maior: rodada deve passar para uma segunda linha quando necessário, sem colisão entre chips.

## Mapa como fundo da batalha — 06/10/2026

Executado assembleDebug e lintDebug, sem tarefas de testes: BUILD SUCCESSFUL em 1 min 23 s; lint 0 erros e 7 avisos. APK atualizado em app/build/outputs/apk/debug/app-debug.apk e instalado no aparelho 24095PCADG com Success; MainActivity abriu. Nenhum teste automatizado adicionado ou executado nesta revisão.

Inspeção real da captura docs/evidencias/battle-background-2026-10-06.png: mesma arte preenche a área de batalha, sem moldura/grade azul central; vida, créditos e rodada sobre o cenário; legenda sobreposta embaixo; coluna direita separada. A escala uniforme recorta bordas conforme proporção, sem distorção. Insets do sistema preservados. PNG original confirmado por SHA-256 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2.

Plataforma abriu preparação e botão Entrar na missão abriu combate via adb input -d 0. A ferramenta não conseguiu selecionar o herói nos toques seguintes: hierarquia permaneceu sem prévia de posicionamento. Não foi possível confirmar visualmente painel de melhorias aberto, compra/venda, pausa/2x ou consistência de posicionamento nesta revisão. Esses fluxos permanecem pendentes no aparelho, sem atribuir a falha de injeção ao jogo. Painel sobreposto e margens de HUD/legenda foram conferidos no código; não se declara fidelidade visual do painel aberto.

Roteiro pendente: na batalha, escolher herói; tocar área livre próxima à rua e confirmar; tocar unidade colocada; conferir melhorias à esquerda sem diminuir/mover cenário, HUD fora do painel e alcance alinhado; fechar painel e comparar cenário; comprar melhoria, vender, iniciar onda, pausar/retomar e alternar 1x/2x. Repetir em outra proporção de tela. Não executar testes automatizados nesta etapa.

Arquivos alterados nesta revisão: app/src/main/res/layout/screen_battle.xml; app/src/main/java/com/example/marveldefence/ui/BattlefieldView.java; game/Viewport.java; MainActivity.java; README.md; docs/PROJETO.md, PROGRESSO.md, VALIDACAO.md, REFERENCIAS.md; evidência PNG acima. Drawable combat_button_green.xml da revisão anterior passou a ser usado nos controles de batalha. Motor, geometria do mapa, imagem, Comic Vine e pop-up preservados.

## Ambiente — 05/10/2026

Leitura de AGENTS/PROJETO/PROGRESSO/VALIDACAO e inspeção de CampaignPager, BattlefieldView, PortalRenderer, Viewport e MapData realizadas antes das alterações. Anexo usado como especificação técnica, com a restrição explícita de preservar padronização, enquadramento, controles e pop-up. Nenhum teste automatizado criado/executado nesta etapa.

Inspeção estática: máscaras específicas por arte não reutilizam polígonos de colisão; aeroporto tem máscara de água vazia; barcos limitados à torre. View ambiental não tem eventos de toque nem controles; camadas Android ficam acima. Filtro de cor aplicado aos efeitos bloqueados, alpha da própria página aplicado a tudo. VisualClock da campanha invalida somente páginas que intersectam a janela; suspensão explícita ao preparar e no onPause; retomada sem recuperar delta ausente. BattlefieldView reutiliza relógio visual para portal e ambiente, abaixo das unidades; 6 s do portal mantidos, sem alteração em PortalRenderer. Esses pontos foram inspecionados em código, não observados em execução.

Imagens derivadas inspecionadas com view_image: cenário conservado fora dos patches locais; duas regiões de água recompostas sem os veleiros parados após ampliar as margens dos patches para retirar restos de contorno. Sprites transparentes com alpha preservado, estilo baseado nos originais. Recursos finais dentro do projeto. Pequenas diferenças de desenho possíveis por extração generativa; não afirmar igualdade pixel a pixel dos sprites.

PNG de combate SHA-256 permanece 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2. Mapa/colisões/motor/API/layouts/drawables do pop-up sem alteração nesta etapa. Apenas integração de suspensão em MainActivity.

Disponibilidade: inicialmente adb devices retornou lista vazia em 05/10/2026; nenhuma AVD retornada por emulator -list-avds. O aparelho reconectou depois: APK instalado com Success e capturas docs/evidencias/environment-tower.png e environment-tower-later.png inspecionadas, com barcos em posições diferentes na água e enquadramento/controles mantidos. Uma gravação de 90 s não pôde ser transferida após desconexão; não foi inspecionada. Pendente: observar pelo menos 90 s (nuvens) e dois circuitos de 40 s dos barcos; verificar margens/mascaras/alinhamento ao redimensionar, legibilidade, gestos/encaixe, pausa/2x independente, segundo plano/retomada sem salto. Desempenho, FPS, fidelidade em movimento e ausência de interferência em toque não confirmados integralmente em aparelho. Plataforma e entrada no combate confirmadas na revisão de 06/10 acima.

Arquivos desta etapa: ui/VisualClock.java, EnvironmentScene.java, EnvironmentRenderer.java, CampaignImageView.java (novos); CampaignPager.java, BattlefieldView.java, MainActivity.java (integração); drawable-nodpi/campaign_tower_environment.png, environment_boat_one.png, environment_boat_two.png (novos derivados); tools/asset-pipeline/prepare-environment.ps1; README e docs/PROJETO, PROGRESSO, REFERENCIAS, VALIDACAO.

### Fechamento do visual do pop-up anterior

Build final da revisão de nomes/drawable do pop-up concluído com BUILD SUCCESSFUL e lint 0 erros/8 avisos. O painel da primeira revisão foi observado no aparelho (build/mission-ui-redesign.png); instalação/conferência da revisão final falhou por desconexão, sem gerar mission-ui-final.png. Preservado esse layout nesta etapa ambiental. Não afirmar nova instalação ou validação final de nomes no aparelho.

Critérios: navegação menu/equipe/partida/resultado; mapa proporcional; toque consistente; posicionamento livre válido/ inválido; cancelamento sem cobrança; saldo/equipe; movimento independente do FPS; pausa sem avanço ausente; ataques, recompensa e escape únicos; cinco ondas sem sobreposição; melhorias/venda/reinício; API em background com erro/cache explícitos e sem chamadas no combate.

## Resultados executados em 04/10/2026
Comando: gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon --console=plain, usando o fallback da JVM descrito no README. Resultado: BUILD SUCCESSFUL. Foram executados 16 testes em GameEngineTest e o teste original ExampleUnitTest: zero falhas e zero erros. A lógica também foi compilada e seus 15 testes iniciais rodaram diretamente com javac/JUnit (antes da verificação adicional de prioridade/intervalo).

| Critério | Resultado | Evidência |
|---|---|---|
| Compilar APK debug | Passou | app/build/outputs/apk/debug/app-debug.apk; assembleDebug |
| Lint | Passou com avisos | app/build/reports/lint-results-debug.html; 0 erros, 5 avisos |
| Posição livre / borda do caminho / faixa / obstáculo / limites / NaN | Passou na lógica | placementUsesPathEdgesObstaclesBoundsAndBand |
| Cobrança uma vez / rejeição / sobreposição / equipe | Passou na lógica | invalidPlacementNeverSpendsAndValidPlacementChargesOnce |
| Saldo insuficiente | Passou na lógica | insufficientFundsDoNotPlaceUnit |
| Evolução de dano e limites | Passou na lógica | damageUpgradeLimitsAndEffectsAreReal |
| Evolução de alcance, venda única e recolocação | Passou na lógica | rangeUpgradeIsIndependentAndSaleOnlyHappensOnce |
| Caminho, curvas amostradas e extremidades | Passou na lógica | pathDistanceAtCornersAndEndpointsIsConsistent |
| Transformação proporcional e inversão do toque | Passou matematicamente | uniformTransformRoundTripsWideAndNarrowScreens; três larguras |
| Pausa do motor | Passou na lógica | pausedSimulationDoesNotAdvance |
| Movimento com diferentes dt | Passou na lógica | equivalentTimeStepsProduceEquivalentTravel; passos 1/60 e 1/30 |
| Escape único, sem recompensa de morte | Passou na lógica | enemiesEscapingCauseDamageOnceAndNeverDropRewards |
| Fora de alcance | Passou na lógica | heroesCannotAttackEnemiesOutsideTheirRange |
| Combate e recompensa única | Passou na lógica | automaticCombatPaysEachKillExactlyOnce |
| Intervalo e prioridade do primeiro alvo | Passou na lógica | attackIntervalAndFirstTargetPriorityAreRespected |
| Teia temporária | Passou na lógica | spiderAppliesTemporarySlow |
| Derrota prioritária e reinício limpo | Passou na lógica | lossBlocksUpdatesAndNewGameIsClean |
| Cinco ondas e vitória, sem atualização posterior | Passou na lógica | fiveWavesEndInVictory |
| API real e dados recebidos | Passou no host | Seis respostas status_code=1, ID e publisher Marvel; powers reais e URLs de imagens em assets/comicvine_profiles.json |
| API/refresh/cache/erro dentro do Android | Não verificado em execução | Cliente compilado; execução em aparelho pendente |
| Dano em área com múltiplos alvos | Implementado, verificação específica pendente | Código GameEngine; ainda requer caso específico e observação da partida |
| Navegação, leitura dos controles, toque real e painel sobreposto | Não verificado visualmente | Layouts e Activity compilados; nenhum dispositivo disponível |
| Aceleração 2x, sair/voltar, recriação e ausência de loops duplicados | Não verificado no Android | Ciclo Choreographer implementado; verificar roteiro abaixo |

## Avisos de lint restantes
1. Orientação fixa: avisos de comportamento em telas grandes/Android 16+. A solicitação é horizontal; a escala do mapa é uniforme. Execução em tablet e modos de janela ainda não verificada.
2. Três ocorrências de pesos aninhados nos layouts de equipe/partida: sugestão de otimização. Não foram suprimidas. Medir em aparelho antes de uma refatoração de performance.
3. Sugestão de merge no FrameLayout raiz: o container é necessário para navegação e aplicação dos insets em runtime. Não é substituído automaticamente.

## Limitação do ambiente
SDK, JVM, Gradle, adb, binário de emulador e imagem de sistema estão presentes. adb devices não mostrou dispositivos; emulator -list-avds não apresentou uma configuração utilizável. AVD presente apenas com arquivos parciais não foi alterado. Não existe captura real da execução do aplicativo e não foi declarada fidelidade visual.

## Roteiro no Android Studio
1. Sincronize o Gradle. Device Manager → Create Virtual Device → aparelho e imagem API 33+ → Finish; inicie o dispositivo e Run app.
2. Menu → Preparar missão. Troque a equipe: no máximo três; tentativa de quarto deve manter três. Ver ficha deve mostrar dados Comic Vine e poderes; abrir a fonte deve levar à página correspondente.
3. Entrar na missão. Confira a imagem completa sem distorção, rua, árvores, água, carros, portal e torre, HUD e painel lateral. Observe drones seguindo o centro da rua nas duas voltas, sem atravessar a ilha central. Compare a composição com as imagens em REFERENCIAS.md. Repita em um telefone e tablet; confira insets e tamanho dos textos.
4. Escolha um herói. Toque no caminho, perto demais, muito longe, em obstáculos e fora do mapa: prévia inválida e confirmação desabilitada. Toque em área livre próxima do caminho: prévia válida. Cancelar mantém créditos; confirmar cobra uma vez.
5. Inicie uma onda. Confira ataque automático, teia e área; drones que escapam reduzem vida uma vez. Onda seguinte só habilita após todos desaparecerem. Créditos só aumentam por mortes e bônus de onda.
6. Toque numa unidade: alcance e melhorias devem aparecer sem deslocar mapa/unidades e sem pausar. Melhore dano/alcance, tente limite/saldo insuficiente, venda uma vez e recoloque.
7. Pausar congela a simulação; Retomar continua. 2x acelera movimento, surgimento, ataques e efeitos conjuntamente. Saia para Home durante a onda, aguarde e volte: deve permanecer pausado, sem recuperar tempo ausente ou duplicar atualização.
8. Complete cinco ondas com equipe/posições adequadas para vitória; outra partida sem defesa deve perder. Jogar novamente deve começar com vida/créditos iniciais e sem unidades/timers antigos. Recriar Activity/processo volta ao menu, como documentado.
9. Atualizar fichas em rede normal, sem rede e com chave ausente/inválida em uma cópia local de configuração: loading, resultado/erro e dados salvos devem estar claros. Nova tentativa respeita dois minutos. Uma partida deve funcionar sem rede.

Estado final: implementação da base concluída com build e lógica verificados; validação visual e dos eventos Android pendente.


## Substituição do mapa — 04/10/2026
Os resultados de 17 testes acima pertencem à base anterior à troca do cenário. Os testes existentes agora usam o mesmo mapa sintético dos cenários anteriores, explicitamente construído no fixture; não foram adicionados nem reexecutados testes nesta alteração.

A imagem original e a cópia no projeto têm SHA-256 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2. O PNG copiado foi inspecionado. Uma exportação de geometria usando as classes Java atuais produziu caminho de 217 amostras, comprimento 1270,37 e 351 pontos livres em uma grade de 15 unidades. A grade é uma ferramenta de configuração, não uma validação visual da partida.

Conferência ainda necessária no Android: imagem completa sem recorte/deformação nas três telas, drones no centro da rua nas curvas, chegada à porta da torre, proibição de posicionar em ruas/água/objetos e confirmação em calçadas/espaços livres. Margens de colisão são conservadoras e podem ser ajustadas após essa conferência. adb devices não mostrou dispositivos. A consulta atual de AVDs não concluiu; a inspeção anterior não havia encontrado um AVD utilizável.

Compilação após a troca do mapa: gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon --console=plain, com fallback JVM do README. BUILD SUCCESSFUL em 54 s. APK atualizado; lint 0 erros e 7 avisos em recursos existentes: orientação, layouts e ícones launcher. Sem execução de testes nesta atualização.



## Portal animado — 04/10/2026
Implementação: espiral procedural limitada à abertura elíptica do portal, duas faixas luminosas, uma volta em seis segundos e brilho central. Bitmap original sem alteração. ValueAnimator visual separado do motor, pausa por visibilidade/foco e cancelamento ao remover a View. Nenhum teste adicionado ou executado nesta atualização.

Conferir em aparelho/emulador: (1) interior girando no menu, equipe e partida; (2) moldura e chifres imóveis, sem desenho sobre a rua; (3) giro sem saltos no fechamento da volta; (4) sem aceleração pelo botão 2x e sem interferir no motor; (5) ir para Home e voltar, abrir/fechar diálogo e navegar repetidamente entre telas, sem animação em segundo plano ou callbacks duplicados. Alinhamento e resultado visual ainda não verificados na execução Android.

Build do portal: :app:assembleDebug :app:lintDebug --no-daemon --console=plain (fallback JVM do README), BUILD SUCCESSFUL em 2 min 19 s. APK atualizado; lint 0 erros e 7 avisos. Resultado visual no Android continua pendente.


## Menu de campanha — resultados de 04/10/2026
Comando executado: gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon --console=plain, com o fallback JVM do README. Build final após correção de dimensionamento: BUILD SUCCESSFUL em 2 min 19 s; 0 erros e 7 avisos de lint (orientação, raiz do layout existente, três pesos aninhados existentes e duas variantes dos ícones launcher). Novos avisos de fundo redundante e ações de acessibilidade foram corrigidos. Nenhum teste automatizado adicionado ou executado; os modelos/análises de fontes de testes do lint não executam casos de teste.

| Item | Verificação real |
|---|---|
| APK debug atualizado | Compilado; app/build/outputs/apk/debug/app-debug.apk |
| Lint do menu final | 0 erros e 7 avisos existentes; relatório app/build/reports/lint-results-debug.html |
| Imagens das regiões bloqueadas | Hashes iguais aos dois anexos fornecidos; sem variantes descartadas no menu |
| Placa TORRE DOS VINGADORES | Correção autorizada via image_gen; resultado inspecionado e salvo no projeto; prompt em REFERENCIAS.md |
| Cenário de combate | PNG com SHA-256 inalterado; motor, BattlefieldView, PortalRenderer e Comic Vine não foram modificados nesta etapa |
| Instalação e abertura no Android | APK final instalado com Success; MainActivity lançada com Status: ok no aparelho modelo 24095PCADG, API 36 |
| Primeira inspeção do menu | Identificado problema de imagens medidas em 1 px; corrigido dimensionamento em onMeasure e recompilado |
| Proporção/rolagem/encaixe/botões após correção | Pendente: dispositivo conectado intermitentemente e outra janela sobre o jogo impediram observar/enviar gestos com segurança. Capturas de outras janelas foram descartadas; não são evidência do aplicativo. |

### Roteiro manual do primeiro menu (histórico, anterior ao pedido de tela cheia)
1. Deixe somente Marvel Defence visível no aparelho, horizontal. Ao abrir, indicador deve mostrar 1/3 e a arte colorida TORRE DOS VINGADORES completa, sem distorção; margens podem aparecer conforme a proporção da tela. Uma faixa desbotada à direita sugere a região vizinha.
2. Deslize menos de meia página e solte: retornar à torre. Deslize mais de meia página: encaixar na Base destruída, indicador 2/3 e Em breve. Repita até Aeroporto, 3/3. Conferir imagens inteiras, cadeado/nome somente na arte, cores apagadas e bloqueio de Preparar missão.
3. Retorne com gestos e com as setas, inclusive gesto interrompendo encaixe. Não deixar página entre índices. Tentar gestos rápidos partindo perto do rodapé: nunca iniciar missão por um gesto.
4. Na torre parada, Preparar missão abre a seleção de equipe existente. Voltar retorna ao menu na torre. Entrar na missão mantém cenário, caminho e portal animado de combate existentes; Comic Vine mantém seu fluxo de fichas.
5. Conferir TalkBack/setas/teclado, insets, janela redimensionada e voltar do segundo plano. Em outro formato de tela, verificar que a imagem continua FIT_CENTER, sem recorte dos elementos principais.

### Arquivos desta alteração
- MainActivity.java: showMenu e import de CampaignPager.
- ui/CampaignPager.java: novo paginador nativo, filtro das prévias, encaixe e acessibilidade.
- res/layout/screen_campaign_menu.xml: novo menu; res/layout/screen_menu.xml removido.
- res/values/strings.xml: textos da campanha e remoção de textos do menu antigo sem uso.
- res/drawable/campaign_arrow_left.xml, campaign_arrow_right.xml e campaign_indicator.xml: controles/indicador.
- res/drawable-nodpi/campaign_tower.png, campaign_destroyed_base.png e campaign_airport.png: três recursos finais.
- README.md, docs/PROJETO.md, docs/PROGRESSO.md, docs/VALIDACAO.md e docs/REFERENCIAS.md: fluxo, arquitetura, estado real da verificação, origem e prompt da arte.

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


## Plataforma verde e preparação em pop-up — 04/10/2026
Implementado: botão Preparar missão removido; plataforma verde existente na arte é o controle nativo de entrada, com hitbox proporcional e mínimo 48 dp. Painel popup_mission.xml sobre a campanha, azul/dourado, sem baús. Seis heróis do catálogo, três slots de equipe, prévia do mapa, Normal/cinco ondas, poderes/fichas e integração Comic Vine mantida. Fechar, Voltar e toque externo mantêm o menu no lugar; entrar usa o combate existente. screen_team.xml e textos antigos sem uso removidos.

Build final: gradlew.bat :app:assembleDebug :app:lintDebug --no-daemon --console=plain, fallback JVM do README. BUILD SUCCESSFUL em 2 min 15 s. Lint 0 erros e 8 avisos: orientação fixa, sugestão de merge da raiz, três pesos aninhados (dois no pop-up e um no combate), sugestão de simplificar o pai do painel e dois ícones launcher. O pai do pop-up mantém área externa clicável e insets independentes do painel. Nenhuma regra suprimida. Nenhum teste automatizado adicionado ou executado.

Conferência manual no aparelho 24095PCADG/API 36:
- Botão textual ausente; mapa em tela cheia e plataforma verde visível.
- Pop-up observado aberto sobre o mapa escurecido, sem troca de Activity/tela de campanha e sem baús; prévia, títulos, seis cartões e controles legíveis.
- Tentativa de quarto herói manteve 3/3. Remover Homem de Ferro e selecionar Thor atualizou os slots; screenshot mission-team-changed.png.
- Fechar com × voltou à campanha; arrasto curto partindo da plataforma ficou no mapa sem abrir o painel; screenshot mission-drag-cancel.png.
- Estado de equipe vazia observado com Entrar na missão desabilitado; screenshot mission-empty-team.png.
- Botão Poderes/fichas abriu a lista de heróis; screenshot mission-powers-list.png. Status de atualização Comic Vine com seis fichas e zero indisponíveis também observado, sem afirmar validação de erro/cache.
- O estado do aparelho mudou entre algumas capturas (equipe zerada e diálogos abertos). Conferência específica da ficha individual e entrada no combate ainda não concluída com estado estável; não confundir capturas de painel/lista com uma partida.
- TalkBack, outros formatos de tela, ciclo de segundo plano e casos de erro da API pendentes.

Mapa de combate: PNG SHA-256 continua 0CC41CA73E10D021CD2A639C3E93C3E2D19C91F19BFD098F563B426B7CEC64F2; GameEngine, MapData, BattlefieldView e PortalRenderer sem alteração nesta etapa.
APK final instalado (Success), MainActivity lançada (Status: ok). Caminho: app/build/outputs/apk/debug/app-debug.apk.

Arquivos de implementação alterados/criados: MainActivity.java, ui/CampaignPager.java, ui/SwipeSafeButton.java, res/layout/screen_campaign_menu.xml, res/layout/popup_mission.xml, res/drawable/mission_panel.xml, res/drawable/mission_platform_touch.xml e res/values/strings.xml; screen_team.xml removido. README.md, PROJETO.md, PROGRESSO.md, VALIDACAO.md e REFERENCIAS.md atualizados. Evidências em docs/evidencias/mission-*.png; nomes das capturas distinguem painel/lista/equipe vazia de combate.

Conferência adicional: ficha individual do Homem-Aranha aberta, exibindo descrição, poderes salvos/atualizados Comic Vine, atribuição e valores de combate provisórios. Hierarquia em build/mission-ready.xml e captura docs/evidencias/mission-spider-profile.png. Isto resolve a pendência da ficha individual; entrada no combate continua sem confirmação nesta etapa.

