
## Conteúdo

```
1.2 Introdução
1.3 As Regras da ONU
1.4 Discussão de alto nível sobre implementação
1.5 Como funciona o jogo principal
1.6 Problemas Conhecidos e Potenciais Melhorias
```
# 1.0 Introdução

Este documento cobrirá a introdução básica do jogo UNO. Todo o jogo foi
escrito sem sprites usando chamadas draw em Java para renderizar tudo na tela. Você pode começar
o aplicativo de Game.java se você mesmo estiver compilando o código.


# 1.2 As Regras da ONU

As Regras do UNO foram todas elaboradas com base na referência ao texto usado na página UNO na Wikipédia <https://en.wikipedia.org/wiki/Uno_(card_game)> e com base no UNO oficial disponível no Steam <https:/ /store.steampowered.com/app/470220/UNO/>.

Com tudo desabilitado nas opções, o jogo base deve imitar o jogo base principal. O
regras que podem ser adicionadas incluem: empilhar +2/+4 cartas, comprar até que uma carta possa ser jogada, por
a reversão de dois jogadores se torna um salto, Seven-0 faz com que o 7 se torne uma troca com o jogador alvo e 0
  torna-se todos passando suas cartas para o próximo jogador, entre para alterar a ordem do turno
  dinamicamente, o jogo forçado exige o jogo de cartas compradas, se possível, sem blefe para
  remova qualquer desafio de cartas Draw Four e limites de pontuação personalizados para determinar o número
  de rodadas.

# 1.3 Discussão de alto nível sobre implementação

Nesta seção, o conteúdo do jogo será descrito brevemente, principalmente mostrando exemplos de imagens de
jogabilidade. Este leia-me não pode mostrar tudo, mas deve dar uma boa ideia dos tipos de
recursos que foram incluídos. Alguns dos gráficos são estáticos e outros são gifs para mostrar
algumas das sequências de animação que ocorrem como parte do jogo.

A primeira imagem mostrada abaixo demonstra o ponto de entrada do aplicativo. O usuário é
imediatamente mostrou uma tela de criação de jogo com muitas opções.

As opções incluíam todas as regras esperadas com algumas restrições menores. As regras que você pode alterar
inclua o seguinte com os botões de alternância à direita:
- Empilhar "Compre +2/+4": (On ou Off), quando ativado você pode responder a +2 ou +4 com o mesmo tipo de carta. A penalidade é acumulada para a próxima pessoa.
- Encontre para pode jogar: (On ou Off), ao comprar do baralho como uma ação, isso força a compra até que uma carta jogável seja comprada.
- Dois/Quatro Jogadores: O reverso torna-se um salto se estiver jogando no modo de dois jogadores.
- Sete-0: (On ou Off), quando o 7 vira troca com um jogador alvo, 0 torna todos os jogadores passando suas mãos para o próximo jogador.
- Pular para: (On ou Off), permite ações de salto durante os turnos de outros jogadores se você tiver uma carta que corresponda exatamente à que está em jogo.
- Jogo Forçado: (On ou Off), força o jogo da carta retirada do baralho se for possível durante uma ação de compra de turno.
- Sem Blefe: Desativa a contestação do "Comprar +4".
- Limite de pontuação: percorre as opções (uma rodada, 200 pontos, 300 pontos, 500 pontos, ilimitado) e controla o que acontece na tela final.

Os outros botões disponíveis na tela incluem “Alternar número de jogadores” para alternar entre 2 e 4 jogadores. Isto altera automaticamente as regras para a regra de dois/quatro jogadores. Para cada um dos jogadores eles também podem ser clicados. Clicar no seu player na parte superior permite que você altere seu nome. Os nomes dos jogadores AI são todos gerados aleatoriamente a partir de uma lista. Clicar em qualquer IA percorrerá estratégias, incluindo (Aleatório, Ofensivo, Defensivo e Caótico). O Aleatório joga qualquer ação aleatória que possa jogar como um movimento válido, O Ofensivo joga para manter as cartas de alto valor (particularmente comprar 4s) até o final da mão, O Defensivo joga primeiro as cartas de alto valor para minimizar a pontuação concedida a um vencedor se perder e mudanças caóticas entre estratégias ofensivas e defensivas. Os dois últimos botões da tela são o "Redefinir para o padrão", que redefine todas as regras para as opções recomendadas, onde apenas as regras de empilhamento e empate até poder jogar estão ativadas com um limite de uma rodada, e o botão "Iniciar jogo" para comece a rodada com as opções especificadas.

Quando o jogo é iniciado, abaixo está uma visão típica do jogo. Mostrando os quatro jogadores com nomes próximos às mãos associadas. Uma cor de nome amarelo/laranja indica o jogador ativo no turno. As mãos dos outros jogadores não são interativas, mas passar o mouse sobre suas próprias cartas aumentará a carta atualmente pairada, como pode ser visto no 4 vermelho na mão do jogador. Os pontos vistos ao redor das cartas no meio giram para mostrar a ordem do jogador (isso ficará mais claro nos próximos gifs). Você pode ver o baralho à esquerda da pilha de cartas. A pilha de cartas no meio mostra a carta do topo e as cartas jogadas anteriormente espalhadas aleatoriamente abaixo dela.

A partir deste estado de jogo, você pode ver que jogar a carta de pular força o próximo jogador na ordem do turno a ser pulado. O jogador depois de Juno aparentemente não tinha nada para jogar, então você pode vê-lo comprando muitas cartas rapidamente. Ter que comprar muitas cartas é resultado da regra "Draw Till Can Play", que exige que um jogador que compre do baralho continue comprando até que algo seja jogável. Então eles podem optar por manter ou jogar a carta. Neste caso, Chance decidiu jogar o curinga e selecionou o verde como cor. As mensagens que aparecem no meio do espaço de jogo mostram claramente quem está executando uma ação necessária em resposta a uma carta e que decisão deve tomar.

Continuando a partir do mesmo estado de jogo mostrado acima, o jogador Denver decidiu jogar um 9 verde. O jogador então escolheu jogar uma carta Empate Dois e foi punido por fazê-lo. Você pode ver como os jogadores pensam em sequência ao aplicar a regra de empilhamento para encadear +2 cartas até chegar ao jogador novamente. Se o jogador tivesse outra carta +2 ele poderia ter continuado a cadeia, mas como não houve resposta o ciclo foi encerrado com o jogador comprando cartas +8 (+2 * 4).

Abaixo pode ser visto o uso de uma carta reversa. Você pode ver os pontos circulando no meio da tela mudando em resposta por um momento. Então o jogador Juno que só tem duas cartas restantes chama UNO! e joga sua própria carta Reverse, enviando-a de volta ao jogador novamente.

# 1.4 Como funciona o jogo principal
Nesta seção descreverei brevemente o propósito de todas as classes incluídas no projeto e depois explicarei o sistema TurnAction e sua integração ao jogo. Você descobrirá que todos os arquivos foram documentados descrevendo sua finalidade, para que você possa procurar nos arquivos individuais detalhes adicionais sobre qualquer recurso.

Aulas genéricas:

Posição: Define uma posição com coordenadas x e y.
Retângulo: define uma região de especificação com posição, largura e altura.
Elementos principais do Windows e da interface genérica:

Jogo: Simplesmente um ponto de entrada para o aplicativo.
PainelJogo: Um JPanel que gerencia a coleção de janelas virtuais exibidas em toda a aplicação.
InterfaceJogo: Um jogo ativo que está sendo jogado com cartas em campo gerenciadas pelos jogadores.
LobbyInterface: Representa a interface para gerenciar as escolhas do número de jogadores e as regras definidas antes de iniciar um jogo.
InterfacePosJogo: A interface mostrada após o término do jogo.
PausaInterface: Mostrado quando o jogo é pausado com Escape. Isso mostra comandos e controles de depuração que podem ser usados pelo player.
WndInterface: A interface genérica usada para todas as janelas mostradas no GamePanel.
Botao: define um botão simples como um retângulo que se renderiza e pode ser verificado ao passar o mouse/clicar.

Sobreposições mostradas durante o jogo:
OverlayGerenciador: gerencia o estado de todas as sobreposições mostradas atualmente.
AntiUnoButton: Um botão mais especializado mostrando o ! para chamar um jogador que não ligou para UNO.
BotaoUno: Outro botão mais especializado para mostrar o UNO para chamar UNO! com apenas 2 cartas restantes.
DesafioOverlay: apresenta a questão de desafio ou recusa para um empate quatro.
FalhaDesafioOverlay: mostra um X sobre o jogador que não conseguiu desafiar o Draw Four.
SucessoDesafioOverlay: Mostra uma marca sobre o jogador que desafiou com sucesso um Empate Quatro.
GeralOverlayInterface: Uma interface que define um método showOverlay() usado para sobreposições que mostram apenas algo sem decisão.
TurnDecisionOverlayInterface: Uma interface que define um método showOverlay com uma referência ao TurnAction que está sendo usado.
PassarOuJogarOverlay: Coloca a questão de manter ou jogar uma carta retirada do baralho.
AnimacaoDirecao: Mostra perpetuamente dois pontos em movimento que circundam o meio da área de jogo para mostrar a direção do jogo.
PlayerFlashOverlay: Mostra todos os flashes de sobreposição dos cartões +X e qualquer um com texto onde a cor é toda igual.
EscolhaDePilha: solicita ao usuário que escolha empilhar ou recusar e sofrer a penalidade.
PlayerSelectionOverlay: Usado para a regra Sete-0 ao jogar um 7 para escolher o jogador com quem trocar de mãos.
ChamadaUno: Pisca UNO! sobre o jogador que chamou UNO.
SeletorCorCuringa: solicita ao usuário que selecione uma cor na roda de cores para usar como cor selvagem.
StatusOverlay: Mostra uma mensagem útil indicando quando algum jogador está fazendo uma escolha relacionada a qualquer uma das outras sobreposições mencionadas acima.

Outras classes:
AIJogador: Estende-se de um objeto Player e define a funcionalidade para jogar como se fosse um jogador humano sem nenhum conhecimento secreto sobre o estado do jogo.

Carta: Define um Cartão incluindo a renderização do cartão.

Deck: Define uma coleção de objetos Card que estão prontos para serem comprados e mostra visualmente o baralho que pode ser clicado como uma ação.

LobbyPlayer: Protótipo para geração dos objetos Player ou AIPlayer permitindo a alteração da configuração relacionada ao nome e estratégia. Responsável também por representar visualmente os elementos do Lobby.

Jogador: Define um jogador genérico que controla uma coleção de cartas com métodos para comprar e gerenciar as cartas em sua mão.

ConjuntoRegras: Define o conjunto de regras que podem ser usadas em um jogo. Isto é usado para a criação das regras no Lobby e depois é mantido para referência no jogo atual.

TurnActionFactory: Usado para gerar as sequências TurnAction para controlar o fluxo do jogo.

Isso conclui as classes que fazem parte do jogo. Vale a pena falar brevemente sobre a implementação do TurnActionFactory e como ele está sendo utilizado para gerenciar o estado do jogo. 

TurnActionFactory.java contém definições da estrutura de dados para construir árvores de listas vinculadas que podem executar uma ação ou aguardar que uma decisão ocorra. Eles são gerenciados na classe "InterfaceJogo.java" principalmente pelo método updateTurnAction() que é responsável por fazer qualquer ação de turno ativa ser executada e então avançar para o próximo estado quando estiver pronta. É importante que exista um conceito de ações de turno enfileiradas porque quando uma cadeia de ações termina ela pode ser continuada com uma nova cadeia com alguns detalhes copiados da anterior. É importante que a sequência de ações pelas quais um jogador é responsável seja limitada ao jogo de uma única carta. Quando uma nova carta é jogada, inicia uma nova cadeia na fila. Isso mantém a ramificação de estados infinitos restritos, especialmente em situações com empilhamento de cartas.

Tudo começa para gerar árvores TurnAction chamando TurnActionFactory.playCardAsAction(int playerID, int cardID, int faceValueID, int colorID) ou TurnActionFactory.drawCardAsAction(int playerID). Isso imita os pontos de entrada para o que um jogador pode fazer no início de seu turno e, particularmente para playCardAsAction, avaliará uma pesquisa nas regras para determinar qual deve ser a ação associada para aquela carta. Isto permite alterar dinamicamente as regras das cartas, como no caso da regra Sete-0. Você pode facilmente obter uma saída de depuração mostrando a árvore construída chamando, por exemplo: debugOutputTurnActionTree(TurnActionFactory.drawCardAsAction(0));

Obter a saída da depuração de playCardAsAction requer que um objeto "InterfaceJogo.java" tenha sido criado com um "ConjuntoRegras". Você pode ativar o modo de depuração e visualizar a saída de depuração das cartas atualmente jogadas durante o jogo enquanto ele é jogado. A árvore maior é mostrada abaixo com a árvore inteira de todas as etapas envolvidas no jogo de uma carta Draw Four. A árvore inclui comentários em sua construção permitindo a geração de informações úteis de status. Para explicar o que é mostrado nesta árvore, cada linha é uma TurnAction separada. As TurnActions que começam com - são executadas como ações e continuam sem qualquer pausa. Aqueles com um? próximo a eles indica que é um nó de decisão. Eles têm duas opções possíveis de divisão com base no atendimento de uma condição. O estado do jogo é mantido nesses nós até que a condição seja atendida. Em alguns casos, isso exige a entrada do usuário ou da IA para tomar uma decisão, como uma resposta à carta que está sendo jogada, ou em outros casos, é simplesmente dividir a árvore de decisão com base em uma variável que já está definida. Isso é comumente usado para tomada de decisões com base em regras.

