# Pesquisa e referências — 04/10/2026

## Estilo compacto — 06/10/2026

Anexos codex-clipboard-89538387-ad07-4edc-a58d-8ffd4d15fe97.png, codex-clipboard-3dccbdeb-973b-4ace-a0cb-ed84ba4667d4.png e codex-clipboard-b8c65937-6da8-43f2-bdf8-bcc28df8b98d.png usados para emblemas, cores, contadores e acabamento dos cartões. Dez ícones locais em VectorDrawable (combat_reactor, shield, lightning, fist, web, widow, energy, range, heart, coin) desenhados em código a partir desses motivos. Controles reais sobre a arte existente, sem substituir cenário nem copiar valores ilustrativos.

## Fundo da batalha — 06/10/2026

Anexos codex-clipboard-1287d1af-a7d3-47dc-afff-b7e6afa98a3a.png (estado anterior), codex-clipboard-0a8e0817-3063-4715-af3e-828a55831f6b.png e codex-clipboard-184bb17f-abdd-4cca-8ee6-0a746bc21450.png (composição pretendida). Aplicados como referência de mapa preenchendo o fundo e controles sobrepostos. Não substituído manhattan_map.png nem incorporadas artes/interfaces rasterizadas desses anexos.

## Combate — 05/10/2026

codex-clipboard-88c493cd-14e2-4051-be5d-9940d866a91b.png e codex-clipboard-907acb83-67b3-4d20-9ce0-cbcc97a5ef80.png: referências de composição/cor do usuário. Interface construída com layouts/drawables nativos; cenário diferente dos anexos não importado, valores ilustrativos não usados no balanceamento, torre tecnológica não usada como personagem. Retratos provisórios reutilizam HeroPortraitView/HeroRenderer do projeto. Sem geração de novas imagens para esta interface.

## Recursos ambientais — 05/10/2026

Skill imagegen, ferramenta integrada, sem CLI/API externa, baseada exclusivamente em campaign_tower.png aprovado. Originais mantidos. Recursos finais em app/src/main/res/drawable-nodpi: campaign_tower_environment.png, environment_boat_one.png, environment_boat_two.png. A variante gerada inteira não é usada como cenário: tools/asset-pipeline/prepare-environment.ps1 aplica somente dois patches locais de água com bordas suavizadas e divide/recorta alpha da folha de veleiros. Fora dos patches os pixels provêm do original. Água/nuvens em runtime são Canvas.

Prompt da água (precise-object-edit; original como edit target): Remove ONLY the two white sailboats at bottom left (near pixels 195,790 and 384,850 of 1672x941). Recompose their water underneath with the same original cyan/blue cartoon river and short wave strokes. Everything else must stay pixel-aligned and visually unchanged: framing, architecture, foliage, rocks, pier, dotted route, green mission platform, golden TORRE DOS VINGADORES plaque, LIBERADO badge, lighting, proportions. No new scenery or objects, no clouds. Output same landscape composition. This is a background derivative for animating separately extracted boats; never redesign the map.

Prompt dos barcos (background-extraction; transparência verdadeira): Extract ONLY the exact two white sailboats from the bottom-left river of attached map, preserving their cartoon outlines, white sails, blue hull highlights, original viewing angle and proportions. Arrange as two separate cutout sprites side by side on actual transparent background, ample transparent gap. Do not include water, wake, pier, foliage, land, text, shadows on water, or any other objects. Do not redesign boats. Preserve their original appearance precisely. Output a tight landscape sprite sheet with the two boats fully visible and no overlap.

Saídas integradas: exec-a59e3615-2a1b-4258-a217-41cf8f8f76aa.png (água), exec-08a0a8b7-0661-4116-a96f-26d212bb814f.png (folha), em .codex/generated_images/01a107b0-3c73-70c1-9879-d02655192337. Sprites 490 × 593 e 499 × 595, exibidos a 95–100 px de largura na arte. Extração generativa pode introduzir pequenas diferenças no desenho; originais disponíveis para futura extração/modelagem manual.

## Bloons TD
Fonte principal: [página oficial da Ninja Kiwi na Steam](https://store.steampowered.com/app/960090/Bloons_TD_6/). Foram consultados a descrição do jogo, as capturas da galeria e as notas oficiais do editor de mapas. Capturas vistas no navegador: mapa desértico com lago e estrada sinuosa; cenário rochoso com água, lava e unidades, com HUD e catálogo à direita. Os nomes dessas fases não foram confirmados.

- [Captura oficial da galeria](https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/960090/ss_a20e85177d96408281e0672927f8e9137c1f423a.1920x1080.jpg?t=1782123772).
- [Outra imagem oficial da galeria](https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/960090/ss_d82f8f2022db8c36bb28393434d6e65dea2d9c64.1920x1080.jpg?t=1782123772).
- [Notas oficiais do editor, versão 39](https://store.steampowered.com/news/posts/?appgroupname=Bloons+TD+6&appids=960090&enddate=1709170182&feed=steam_community_announcements): caminhos em pontos, largura própria, terreno, objetos e áreas que bloqueiam posicionamento.

## Aplicação ao protótipo (decisões deste projeto)
Rua sinuosa sobre o cenário enviado pelo usuário; água, árvores, carros e prédios como obstáculos; portal de Loki e Torre dos Vingadores como entrada e base; controles e fichas como Views Android funcionais. Composição: HUD no topo, campo central e equipe à direita. Painel de evolução sobreposto à esquerda preserva o viewport.

O universo Marvel aparece na Torre dos Vingadores, portal de Loki, drones e funções provisórias dos heróis (repulsor, teia, impacto, trovão, escudo e precisão). A estratégia vem de cobertura do caminho, custo de implantação, intervalos e evolução de unidades. Esta base não tenta reproduzir as camadas de balões ou todas as modalidades de Bloons.

## Imagens do usuário
As sete capturas anexadas foram vistas na conversa: preparação, fases, resultado, partida e evolução. Usadas para composição/interação. Personagens Adventure Time e mão de tutorial não foram usados. Arquivos originais continuam em Downloads.

## Origem da arte e dados
O mapa inicial desenhado em Canvas foi substituído pela imagem que o usuário pediu para utilizar como fundo em 04/10/2026. Arquivo recebido: codex-clipboard-b5d77e3f-2f05-4b9e-bde3-5f14bde04755.png; cópia sem alteração em app/src/main/res/drawable-nodpi/manhattan_map.png, 1672 x 941. Autoria externa não informada. UI, drones e pequenos personagens continuam desenhados em XML/Canvas neste projeto, como arte provisória. Modelagem do usuário substituirá HeroRenderer; nenhuma imagem de catálogo é tratada como animação.

Fichas: [Comic Vine](https://comicvine.gamespot.com/api/documentation). Seis respostas reais consultadas nesta data, com publisher Marvel e IDs conferidos: Iron Man 1455, Spider-Man 1443, Hulk 2267, Thor 2268, Captain America 1442, Black Widow 3200. Dados salvos em assets/comicvine_profiles.json para uso offline. URL de imagem armazenada, mas o campo usa arte provisória própria. A lista powers inclui categorias como Super Strength e Intellect; o esquema character não contém uma grade numérica de atributos.

## Android
[Desenho em View/Canvas — documentação oficial](https://developer.android.com/develop/ui/views/layout/custom-views/custom-drawing). Desenho separado do motor, objetos de Paint reaproveitados e terreno estático no bitmap fornecido pelo usuário, sem geração a cada frame. Escala e toque usam a mesma transformação.


## Arte final do menu de campanha
Fontes: os três anexos fornecidos pelo usuário nesta etapa; não foi selecionada outra imagem dos Downloads ou variante descartada. Recursos finais em app/src/main/res/drawable-nodpi:
- campaign_tower.png: anexo codex-clipboard-8c8186c5-d9c5-4688-bb94-2f9e4ed6c7b2.png, com correção da placa autorizada pelo usuário: “muda para torre dos vingadores, eu coloquei mahattan por engano”. Correção feita pela ferramenta image_gen integrada, com skill imagegen; resultado inspecionado antes de integração. Arte sem personagens; o estado LIBERADO foi mantido.
- campaign_destroyed_base.png: cópia do anexo codex-clipboard-cb77af3c-8eb5-4c31-a2e9-2c88e8cf3326.png.
- campaign_airport.png: cópia do anexo codex-clipboard-dfdb50c0-97e7-404a-ba01-5fff9eff6023.png.

Prompt final da correção (ferramenta integrada, sem CLI):
> Use case: text-localization. Edit target: the attached campaign illustration. Change ONLY the black lettering on the golden bottom banner from 'PARQUE DE MANHATTAN' to the exact text 'TORRE DOS VINGADORES', all caps, same bold cartoon serif lettering, centered on the same existing banner. Preserve the 'LIBERADO' green badge unchanged. Keep the whole original city island, Avengers tower, pond, trees, water, boats, dotted route, green mission node, bridge, faded neighboring region, colors, lighting, camera, composition, golden banner shape, outlines and proportions unchanged. Do not crop, add characters, add locks, add UI, or redesign any part. This is a precise correction of the existing label only. Keep original landscape aspect ratio approximately 1672:941.

As prévias bloqueadas recebem filtro de cor somente em runtime, sem editar os PNGs. O mapa de combate manhattan_map.png permanece separado e inalterado. Menus não reutilizam a arte do mapa de combate para essas três regiões.

Preparação da missão: os anexos codex-clipboard-dadf2ca2-50e0-431c-b4e8-67e78f9add06.png e codex-clipboard-174662f8-aa41-4c5a-aa17-0f55569682a8.png orientam o ponto verde e a composição do pop-up. A plataforma já está na arte aprovada; somente sua área de toque foi adicionada. Painel e controles feitos com Views/drawables Android, em azul e dourado; baús, personagens e moldura da referência não foram copiados.


UX/UI do pop-up: codex-clipboard-55c5b3bc-efde-403a-80d2-ae5bf12b1550.png, fornecido pelo usuário. Referência aplicada em Views e drawables Android: painel claro, faixa azul, seções tracejadas, cartões e ações amarelo/azul/verde. Emblemas simples são provisórios; mapa da prévia é o recurso de combate já aprovado, não a variante ilustrada no mockup.


## Card de divulgação — 07/10/2026
Arte gerada com a skill imagegen/ferramenta integrada a partir da logo art/marvel-defence-logo.png e do mapa app/src/main/res/drawable-nodpi/manhattan_map.png, ambos deste projeto. Prompt solicitou card horizontal com título MARVEL DEFENCE, percurso da esquerda para a direita, drones com barras de vida, três unidades em plataformas com alcance e ataques e base à direita. Recurso final: art/card-divulgacao-marvel-defence.png. É ilustração promocional, não captura de jogo.


Card revisado: composição SVG com captura anexada codex-clipboard-d4fd50bc-0a00-4d48-a568-1153c894ab12.png e logo art/marvel-defence-logo.png; rasterizada em PNG com sharp. Fonte e PNG em art/card-divulgacao-marvel-defence-original.svg e art/card-divulgacao-marvel-defence-captura.png. A captura real é preservada dentro do card, sem inventar torretas.


Card promocional final com heróis: edição via imagegen do primeiro card art/card-divulgacao-marvel-defence.png; referência visual adicional na captura de combate enviada pelo usuário em 07/10/2026. Prompt preservou título, mapa, drones, composição e efeitos e substituiu somente três máquinas/pedestais por personagens em pé. Arquivo art/card-divulgacao-marvel-defence-herois.png. Ilustração conceitual, não screenshot.

