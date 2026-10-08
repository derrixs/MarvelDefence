# Marvel Defence — base Android

Protótipo nativo em Java para você modelar os personagens e ajustar as regras depois. Interface horizontal, mapa urbano enviado por você, do portal de Loki à Torre dos Vingadores, até três heróis por equipe, posicionamento livre, cinco ondas de drones, ataques automáticos, melhorias de dano/alcance e venda.

## Abrir e executar
1. Abra esta pasta no Android Studio e sincronize o Gradle.
2. Mantenha as versões já configuradas pelo projeto: Gradle 9.6.0, AGP 9.4.1 e compile/target SDK 37. A JVM do daemon está configurada em gradle/gradle-daemon-jvm.properties.
3. Use um emulador ou aparelho Android 13 (API 33) ou superior. Se necessário, Device Manager → Create Virtual Device → escolha um aparelho e imagem API 33+ → Finish → inicie o dispositivo.
4. Selecione a configuração app e clique Run.

Fluxo: menu de campanha começa na Torre dos Vingadores → deslize entre três regiões ou use as setas (Base destruída e Aeroporto: Em breve) → tocar na plataforma verde da torre → escolher até três heróis no pop-up → Entrar na missão → selecionar herói à direita → tocar no mapa → Confirmar → Iniciar onda. Toque numa unidade para melhorar ou vender. O próximo botão de onda habilita após todos os drones da onda saírem. No fim, Jogar novamente cria uma partida limpa.

Na primeira abertura, a capa com a logo aparece brevemente antes do menu; toque ou Voltar a dispensa. O launcher usa o ícone MD adaptativo.

Ao sair do aplicativo, a simulação para; ao voltar durante uma onda, toque Retomar. Partida permanece em memória durante pausa; recriar Activity ou encerrar processo volta ao menu. Ainda não existe salvamento de partida.

## Onde editar
- app/src/main/java/com/example/marveldefence/ui/CampaignPager.java: páginas da campanha, rolagem, encaixe, acessibilidade e estado bloqueado.
- app/src/main/res/layout/screen_campaign_menu.xml: controles e disposição do menu principal.
- app/src/main/res/drawable-nodpi/campaign_*.png: imagens finais da campanha; correção da placa da torre documentada em docs/REFERENCIAS.md.
- app/src/main/java/com/example/marveldefence/data/HeroCatalog.java: equipe de demonstração, cores, funções e atributos de combate.
- app/src/main/java/com/example/marveldefence/game/GameConfig.java: regras provisórias, ondas, custos de evolução, venda e colocação.
- app/src/main/res/drawable-nodpi/manhattan_map.png: fundo original do mapa (1672 x 941).
- app/src/main/java/com/example/marveldefence/game/MapData.java: caminho e obstáculos; pontos em pixels da imagem convertidos pelo fator 1000/1672.
- app/src/main/java/com/example/marveldefence/ui/HeroRenderer.java: substitua os desenhos pelos seus sprites/modelagens renderizadas em 2D. Preserve o tamanho lógico configurado para colisão.
- app/src/main/java/com/example/marveldefence/ui/BattlefieldView.java: exibição proporcional do fundo, heróis e inimigos; transformação comum para desenho e toque.
- app/src/main/java/com/example/marveldefence/ui/PortalRenderer.java: espiral verde do portal; posição, inclinação, tamanho e brilho. O período de giro (6 segundos) fica em BattlefieldView.
- app/src/main/res/layout: telas e controles.
- app/src/main/res/values/strings.xml: textos.

## Comic Vine
A chave fornecida está configurada no local.properties ignorado. Para usar outra chave local: adicione comicvine.api.key=SUA_CHAVE nesse arquivo e sincronize/recompile. O debug lê essa configuração; release usa chave vazia. Não há chave em arquivos de código/documentação.

As seis fichas incluídas são respostas reais da Comic Vine, com identidade, resumo, poderes e URLs de imagem. Na preparação, Ver ficha mostra os poderes registrados e um link de atribuição à página original. Atualizar fichas consulta a API em background e preserva os registros salvos em caso de erro. Há limite local de uma tentativa a cada dois minutos (persistido entre aberturas), uma fila única e intervalo entre consultas. Sem atualização, o protótipo funciona offline com as fichas salvas.

Comic Vine não entrega notas numéricas de força/inteligência nesse esquema. Categorias como Super Strength/Intellect são mostradas conforme retornadas. Dano, alcance e custos são valores provisórios do jogo, independentes da API. Imagens de catálogo não são usadas como sprites. Consulta de catálogo não modifica uma partida em andamento.

## Verificação
Comandos PowerShell na raiz:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Neste ambiente Windows, o seletor da JVM encontrou um erro de socket Unix local. A execução nesta sessão usou o fallback TCP apenas no processo, sem mudar a JVM configurada:

```powershell
$env:JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=C:\__marveldefence_no_socket_dir__'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon --console=plain
```

O diretório acima deve permanecer inexistente para a JVM usar o fallback. Use esse ajuste somente se encontrar o mesmo erro. Não modifica configurações globais.

Consulte docs/VALIDACAO.md para os resultados reais, verificações pendentes e roteiro no emulador. docs/PROJETO.md descreve arquitetura e decisões provisórias; docs/REFERENCIAS.md registra a pesquisa e a origem da arte/dados.



O menu de campanha preenche a tela: escala uniforme com recorte do excedente superior, placa inferior preservada e controles Android sobre a arte. Equipe e combate mantêm o enquadramento anterior.


Preparação atual: toque na plataforma verde da Torre dos Vingadores para abrir o pop-up sobre o mapa. Selecione até três heróis, consulte Poderes/fichas e toque Entrar na missão. Fechar ou Voltar retorna ao mesmo mapa de campanha.

Combate: mapa aprovado como fundo preenchendo a área de batalha, com escala uniforme e recorte das bordas conforme o formato do celular. HUD/legenda sobrepostos; retratos provisórios à direita e melhorias sobre a esquerda. Verde inicia onda; vermelho pausa/retoma; velocidade e saída permanecem acessíveis. Retratos usam recursos existentes, sem downloads durante a missão. O pop-up continua com prévia proporcional completa.

Ambiente: água com ondulações discretas nas áreas mascaradas, dois veleiros no rio exterior da torre e nuvens periféricas suaves. O ambiente da campanha suspende com a preparação aberta; prévia e portal continuam animados. Configuração visual em ui/EnvironmentScene.java, separada do motor. Originais preservados; recursos derivados e origem em docs/REFERENCIAS.md. Conferência dinâmica pendente enquanto não houver aparelho/emulador disponível.

