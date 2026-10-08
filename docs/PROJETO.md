# Projeto

## Identidade de abertura e launcher — 07/10/2026

O ícone adaptativo Android usa a proposta quadrada MD em fundo azul escuro, com versão monocromática vetorial para ícones temáticos. A primeira abertura exibe por até 1,8 s uma capa com a logo completa e permite dispensá-la com toque ou Voltar; depois segue para a campanha existente. A capa não reaparece ao voltar da missão ou recriar a Activity. Artes geradas para este projeto em art/marvel-defence-app-icon.png e art/marvel-defence-logo.png; recursos derivados em drawable-nodpi. Ainda falta inspeção visual em aparelho.

## Revisão de layout e espaçamento do combate — 07/10/2026

Referências visuais: três recortes e duas composições completas anexadas em 07/10 (6c0b5089, afbd8c32, 45a09213, 43564ead e 456b3ecc). Interface construída com Views Android, XML e vetores locais. Barra direita de 112 dp agora tem fundo azul mais escuro e cartões de 72 × 72 dp, com emblemas de 40 dp centralizados por padding simétrico e intervalo de 6 dp. Somente a equipe selecionada é exibida; estados e valores continuam reais.

Painel esquerdo de 176 dp, sobreposto e rolável: padding externo 8 dp; retrato com conteúdo afastado 8 dp da moldura e fechar afastado 4 dp; alvo/estatísticas com padding explícito; cartões claros/dourados com ícone à esquerda e título, nível e preço à direita. Cada cartão tem 7 dp de padding, intervalo de 6 dp entre ícone/texto e 7 dp entre cartões. Textos têm altura variável, sem ellipsis; preços, venda, ficha e demais ações mantêm alvos de pelo menos 48 dp. Em alturas pequenas o painel exige rolagem para acessar todas as ações, preservando legibilidade.

HUD com altura variável e mínimo de 36 dp por cápsula para acomodar os ícones de 28 dp. Uma verificação da largura natural dos textos/paddings reorganiza a rodada numa segunda linha quando necessário; não reduz a fonte para encaixar. HUD/legenda continuam deslocados para fora do painel ao abri-lo. Confirmar/cancelar têm separação de 4 dp. Mapa/viewport, geometria, motor, regras, campanha, preparação e Comic Vine preservados. Verificação executada e limitação de capturas em VALIDACAO.md; fidelidade visual final ainda não confirmada.

## Estilo compacto de combate — 06/10/2026

Aplicadas referências 89538387, 3dccbdeb e b8c65937: barra direita de 112 dp com botões nativos de 56 dp e emblemas vetoriais de reator, escudo, raio, punho, teia e capuz. Exibe somente a equipe escolhida; estados de seleção, colocado e saldo mantidos. Nome/custo/estado em tooltip e descrição acessível. HUD compacto com ícones próprios de coração/moeda, fonte pesada e contadores reais; melhorias com ícones de energia/alcance em cartões claros/dourados, venda vermelha e ações verdes/vermelhas. Painel esquerdo 176 dp sobreposto; mapa atual, portal, motor, regras, Comic Vine, campanha e pop-up preservados. Botões inferiores mantêm pelo menos 48 dp. Arte dos emblemas criada em VectorDrawable, sem rasterizar a interface das referências.

## Mapa como fundo do combate — 06/10/2026

Pedido posterior substitui a apresentação do mapa inteiro dentro da moldura azul: a mesma manhattan_map.png agora preenche a área de batalha como fundo. screen_battle.xml usa FrameLayout, BattlefieldView na base e HUD/legenda Android sobrepostos, sem reservar faixas superior/inferior. Coluna direita mantém 136 dp; painel esquerdo de 184 dp abre sobre o cenário, como na referência, sem encolher o mapa. Ao abrir, HUD e legenda ganham margem esquerda para não ficar sob o painel.

Viewport.cover usa escala uniforme pelo maior fator e centralização, com recorte das bordas conforme proporção do aparelho; sem esticar ou editar o PNG. Substitui o requisito anterior de exibir toda a imagem em todos os formatos. Desenho, efeitos ambientais, portal, unidades, alcance e toque continuam na mesma transformação inversa. Preview no pop-up mantém Viewport.fit e enquadramento anterior. Retirada a grade/fundo azul decorativo central; azul permanece nos controles/painéis. Geometria, coordenadas das unidades, motor e Comic Vine preservados.

## Interface de combate azul/ciano — 05/10/2026

Referências do usuário para layout, sem substituição de manhattan_map.png. screen_battle.xml agora usa três colunas: melhorias em ScrollView à esquerda (GONE inicialmente), mapa/HUD/legenda ao centro e heróis/ações à direita. HUD de vida/créditos/rodada em faixa própria fora da imagem. Ao abrir melhorias a largura disponível do BattlefieldView muda e Viewport.fit recalcula escala uniforme para o mapa completo; sem alterar coordenadas de unidades ou geometria. A legenda está fora da View do mapa; removida a reserva inferior antiga de 42 dp. Espaços residuais têm acabamento azul/grade discreta não jogável; toques fora dos limites da imagem não são enviados ao motor.

Cartões azuis/ciano usam HeroPortraitView/HeroRenderer já existentes (retratos vetoriais provisórios dos personagens, sem símbolos do mockup), equipe escolhida e custos reais. Borda ciano = disponível; verde = selecionado; dourada = colocado; cinza/desabilitado = saldo insuficiente para colocar. Colocado continua selecionável mesmo sem saldo. Descrições acessíveis incluem nome, estado e custo; ação mínima 48 dp; lista com rolagem vertical. Não há consultas/downloads de retratos no combate; Comic Vine possui metadados/URL, sem cache de imagens local, por isso usa-se o recurso visual já disponível.

Painel esquerdo: retrato maior, dano/alcance reais, prioridade atual escrita sem setas, cartões claros/dourados para dano e alcance, níveis/preços reais, compras indisponíveis desabilitadas, venda vermelha com valor real, fechar e ficha existentes. Retrato recriado somente ao mudar personagem, não a cada atualização do HUD. Iniciar onda verde, pausa/retomada vermelha com ícone/descrição de estado, 1x/2x e saída compactos. Portais/ambiente mantidos abaixo de unidades/HUD. Insets existentes preservados. Nenhuma regra/economia/onda/colisão/API modificada.

## Animações ambientais — 05/10/2026

EnvironmentScene centraliza máscaras visuais independentes das colisões em pixels 1672 × 941, opacidades, quantidades e trajetos. EnvironmentRenderer reutiliza Paint/Path/RectF e sprites decodificados na criação: ondulações/reflexos/brilhos somente dentro das máscaras; nuvens Canvas com alpha 32/255 (12,5%), recortadas nas bordas decorativas; dois veleiros somente na campanha da torre. Aeroporto sem água. Base com ondas no rio, sem barcos. Combate/prévia com água e nuvens restritas à periferia.

CampaignImageView aplica a mesma imageMatrix do fundo, filtro das regiões bloqueadas e alpha da página. O único VisualClock de CampaignPager atualiza somente páginas que intersectam a largura visível do ScrollView. Preparação suspende o ambiente da campanha; a prévia continua animada. Faixas vizinhas estáticas. Layouts, enquadramento e controles preservados.

BattlefieldView compartilha relógio visual contínuo entre ambiente e portal: ângulo = (segundos % 6) × 60, mantendo PortalRenderer/período. Ambiente após terreno, antes de portal/unidades/alcance/efeitos de combate, na mesma transformação da imagem. Independente de pausa/2x. Background, foco/visibilidade e detach removem callbacks; retomada não acumula tempo ausente. Não há animator por elemento nem decodificação em onDraw.

Derivados: campaign_tower_environment.png conserva o original fora de dois patches de água locais para remover barcos; environment_boat_one/two.png são recortes transparentes baseados nos veleiros originais. Originais mantidos. Barcos fazem circuitos locais elípticos de 40 s na água exterior, deslocamento ±23/±7 px, balanço ±1,2° e pequena esteira. Composição conservadora, distante do píer/placa; sem barcos em lago, base, aeroporto ou combate. Origem em REFERENCIAS.md.

Não alterados nesta etapa: pop-up/drawables, PortalRenderer, Viewport, MapData, GameEngine, colisões, caminhos de inimigos, catálogo, balanceamento e Comic Vine. Validação dinâmica/desempenho ainda exige aparelho: ver VALIDACAO.md.

## Pedido confirmado
Base Android Java nesta pasta. Referências: Bloons TD e as imagens anexadas de Adventure Time TD. Universo Marvel; personagens serão modelados pelo usuário depois. Posicionamento livre, equipe antes da fase, ataques automáticos, caminho até a base, controles superiores e painel direito. API informada: Comic Vine; uso local da chave autorizado.

## Provisório
Um mapa: Torre dos Vingadores, usando a imagem enviada pelo usuário em 04/10/2026. Entrada pelo portal de Loki e saída na porta da torre. Uma dificuldade, equipe até três, uma unidade por herói, seis heróis de demonstração, três tipos de drones, cinco ondas. Vida inicial 100, créditos 1000. Custos, danos, alcances, melhorias e venda serão centralizados em GameConfig/HeroCatalog. Não são decisões definitivas de balanceamento.

## Arquitetura
Activity única e layouts XML para menu, preparação em pop-up e partida. Campo em View/Canvas: adequado à pequena base 2D e permite ciclo Choreographer e eventos de toque na mesma thread. Não é necessário SurfaceView/Fragments nesta dimensão; renderer e motor continuam separados para migração futura. Motor Java puro, passo fixo 1/60, recuperação limitada, pausa de ciclo ao sair. Coordenadas lógicas 1000 x (941 × 1000 / 1672), aproximadamente 1000 x 562,80 com escala uniforme e margens. Painel de melhorias sobreposto sem modificar coordenadas ou viewport do mapa.

Pacotes: data (catálogo/configuração da API), game (mapa, geometria, motor), ui (desenho do mapa/heróis). API carrega identidade, descrição, lista de poderes e URL da imagem. Imagem de catálogo não é sprite. Modelagem futura entra em HeroRenderer; balanceamento entra em HeroCatalog/GameConfig. API não cria heróis jogáveis automaticamente.

## Comic Vine
Consulta real de Iron Man autorizada e bem-sucedida (status_code 1). Endpoint character/4005-ID, field_list id,name,deck,powers,publisher,image,site_detail_url. Não fornece notas numéricas de força/inteligência nos campos documentados: mostrar categorias de poderes reais; não apresentar números locais como dados da API. Combate usa configuração independente.

## Regras implementadas da demonstração
- Caminho traçado sobre a rua da imagem com nove curvas cúbicas, 24 amostras por curva e largura lógica de bloqueio 68; raio ocupado do herói 21; margem adicional 9; faixa máxima de colocação 180 a partir da borda. Movimento e colisão usam os mesmos segmentos. Água, prédios, portal, carros e ilha central usam polígonos; árvores usam círculos. A margem de colocação é conservadora e pode ser ajustada após uso em aparelho.
- Dano por tiro/golpe é instantâneo. Linhas de ataque são efeitos visuais, não projéteis. Prioridade: inimigo vivo com maior progresso dentro do alcance.
- Área: raio 48 em torno do alvo, limitado ao alcance do herói. Teia: velocidade 55% durante 1,5 s; repetir renova duração sem acumular multiplicadores.
- Melhorias: três níveis de dano (+40% do dano base por nível) e três de alcance (+25 por nível), independentes por unidade.
- Venda: floor(70% do investimento em colocação + melhorias). Venda libera posição e permite recolocar a mesma identidade.
- Onda seguinte é manual, após surgimento de todos e zero inimigos ativos. Bônus 100 uma vez. Derrota tem prioridade sobre fim de onda; resultado congela combate. Reinício cria motor novo.
- Ao abrir ficha/evolução, simulação continua. Ao tentar sair da missão, diálogo pausa temporariamente. Ao sair do app, ciclo para; ao voltar à onda, Retomar é explícito.
- API: atualização manual, uma fila, timeout e cache preservado por personagem em erro. Intervalo de dois minutos entre lotes de seis, persistido entre aberturas. Sem paginação nesta base: seis consultas de detalhe com IDs fixos, não uma busca global.

## Pendências
Arte/modelagem final, elenco final, cenário definitivo, balanceamento, habilidades especiais, outras fases/dificuldades, persistência de partida e recuperação de processo. A base tem reinício limpo ao recriar Activity/processo. Não há login, multiplayer, loja ou monetização.

## Referências
Consultar docs/REFERENCIAS.md para pesquisa e origem visual. Mão de tutorial e personagens Adventure Time não entram no jogo.

## Mapa enviado pelo usuário
Fundo original em app/src/main/res/drawable-nodpi/manhattan_map.png (1672 x 941), copiado sem alteração. Bitmap desenhado com escala uniforme, sem recorte ou deformação. Equipe e partida usam esse cenário; o menu principal agora usa as três imagens de campanha descritas abaixo. Trajeto e obstáculos em MapData são definidos em pixels da arte e convertidos pelo fator 1000/1672; BattlefieldView usa as dimensões do mesmo mapa tanto para desenho como para toque. Uma geometria explícita pode ser passada ao construtor de MapData para outros mapas e para cenários determinísticos do motor.


## Portal animado — 04/10/2026
PortalRenderer desenha uma sobreposição procedural em Canvas somente dentro da abertura elíptica verde do portal. Centro/tamanho/inclinação são definidos em pixels da arte e convertidos à mesma escala do mapa; moldura, chifres e PNG permanecem fixos. Duas espirais giram com interpolação linear, uma volta a cada seis segundos, com brilho central suave.

BattlefieldView mantém um único ValueAnimator visual por mapa, independente dos relógios/regras do motor. Funciona na prévia de equipe e entre ondas; o novo menu de campanha usa imagens estáticas próprias. A pausa de combate e o botão 2x não alteram o giro decorativo. O animator pausa quando a View/janela deixa de ser visível ou perde foco, retoma ao retornar e é cancelado ao remover a View. Caminhos, tintas e gradientes são reaproveitados entre frames; sem novos bitmaps por frame.

## Menu principal de campanha — 04/10/2026
Entrada principal: screen_campaign_menu.xml e CampaignPager, um HorizontalScrollView Android nativo com três páginas, na ordem Torre dos Vingadores em Nova York (liberada), Base dos Vingadores destruída e Aeroporto da Guerra Civil (bloqueados). Ao abrir/retornar ao menu, começa na torre. As duas prévias não possuem fases jogáveis ou desbloqueio por progresso.

Após o pedido de preencher toda a tela, cada página ocupa a largura e altura totais. A imagem usa matriz de escala uniforme para cobrir a tela, centralizada horizontalmente e alinhada à borda inferior para manter a placa visível. Isso elimina margens, sem esticar a arte; em celulares largos há recorte da parte superior. Os PNGs aprovados não foram modificados. Pistas dos vizinhos são sobreposições estreitas de 16 dp com alpha 0,30. Imagens bloqueadas têm filtro de saturação 0,70 e luminosidade 0,88. Nomes, cadeados e status existentes na imagem não são repetidos como títulos/ícones da interface.

Controles Android sobre a arte: plataforma verde tocável na torre, Em breve no canto superior direito das prévias, indicador no topo e setas nos cantos inferiores. A preparação abre em pop-up sobre a campanha, conforme atualização abaixo. Controles respeitam insets/câmera; a arte passa por trás. Menu oculta barras do sistema, recuperáveis com gesto; equipe e combate mantêm barras e insets existentes. Rolagem encaixa no índice mais próximo ao soltar, sem inércia que pule regiões, com transição de 240 ms. Novos gestos cancelam o encaixe anterior; mudança de tamanho preserva o índice. Preparar missão exige índice zero, ausência de gesto/encaixe e alinhamento completo. SwipeSafeButton cancela o clique quando a pressão se transforma em arrasto, inclusive dentro do próprio botão. Setas, ações de acessibilidade e teclado também usam o encaixe.

MainActivity constrói o menu e aplica tela cheia somente na campanha; equipe, combate e Comic Vine mantêm o fluxo existente. BattlefieldView, PortalRenderer, MapData e o PNG de combate não foram alterados nesta etapa. O menu usa arte estática de campanha; a animação do portal permanece na seleção de equipe e no combate.


## Preparação sobre a campanha — 04/10/2026
Pedido: remover Preparar missão do canto da tela, usar a plataforma verde como entrada e abrir um pop-up com temática dos Vingadores, inspirado na composição da referência e sem baús.

Implementação: CampaignPager mantém um botão Android transparente com ripple sobre o círculo verde já presente na arte. Posição e tamanho seguem a matriz da imagem da torre; área mínima de toque 48 dp. Gesto de arrasto cancela clique e o pop-up só abre com a região liberada encaixada. As prévias continuam bloqueadas.

popup_mission.xml é uma sobreposição na mesma Activity: mapa da campanha permanece visível e escurecido atrás. Moldura azul/dourada, título Central dos Vingadores, prévia do mapa de combate com portal, dificuldade Normal (cinco ondas), seis heróis em grade, até três slots selecionados, Poderes/fichas, atualização Comic Vine e Entrar na missão. Sem baús, recompensas, dificuldades novas ou desbloqueios. Arte dos personagens e balanceamento permanecem provisórios.

Fechar, toque fora do painel e Voltar removem apenas o pop-up e mantêm a campanha; seleção da equipe permanece na sessão. Ao entrar, usa o mesmo GameEngine/MapData/BattlefieldView. Fichas e fila/cache/intervalo Comic Vine preservados; callback de atualização consulta se o pop-up ainda existe. Conteúdo do mapa atrás fica fora da árvore de acessibilidade enquanto o pop-up está aberto.

Arquivos: MainActivity.java; ui/CampaignPager.java; res/layout/screen_campaign_menu.xml e popup_mission.xml; res/drawable/mission_panel.xml e mission_platform_touch.xml; res/values/strings.xml. Nenhum teste automatizado adicionado ou executado. Resultados de build/lint e inspeção manual registrados em VALIDACAO.md.

## Novo visual do pop-up — referência UX/UI de 04/10/2026
Aplicado o layout do anexo codex-clipboard-55c5b3bc-efde-403a-80d2-ae5bf12b1550.png: painel claro com borda/sombra azul, faixa azul com título branco, seções azul-claro com contorno tracejado, prévia à esquerda, seis cartões em duas linhas à direita e indicador de equipe no canto da seção. Cartões têm emblemas simples, nomes e ação +/✓; seleção também muda o contorno para verde. O estado real da equipe é preservado, sem zerar somente para igualar o exemplo 0/3 da referência.

Rodapé: Normal/cinco ondas, Poderes/fichas amarelo, Atualizar fichas azul e Entrar na missão verde. Status Comic Vine abaixo das ações. Botão de fechar explícito; mapa da campanha escurecido e desfocado usando RenderEffect (minSdk existente 33), restaurado ao fechar. Controles Android reais; emblemas e fundos feitos com TextViews/drawables, sem usar o mockup como imagem de interface. A prévia mantém o mapa de combate aprovado, sem substituir pela variante do mockup.

Não alterados: limite de três heróis, catálogo/balanceamento, Comic Vine, motor, mapa de combate e animação do portal. Sem baús. Removida a faixa adicional de nomes selecionados, pois +/✓ e contorno nos cartões já representam a equipe como na referência. Tamanho mínimo das ações dos cartões 48 dp; o cartão inteiro também alterna seleção.

Arquivos: MainActivity.java, res/layout/popup_mission.xml, res/values/strings.xml, res/drawable/mission_panel.xml; novos mission_section.xml, mission_card.xml, mission_badge.xml, mission_add.xml, mission_ribbon.xml e mission_button_yellow/blue/green.xml. Resultados de compilação/lint e inspeção registrados em VALIDACAO.md. Nenhum teste automatizado adicionado ou executado.
