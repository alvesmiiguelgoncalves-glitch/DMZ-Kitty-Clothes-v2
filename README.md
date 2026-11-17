# DMZ: Kitty's Clothes

Addon para o **DragonMine Z 2.2** (Forge 1.20.1) com roupas como itens de **Curios**, cada uma num slot próprio. Assim você usa as roupas junto com qualquer armadura. Hoje inclui a **Jaqueta S.A.B.**; luvas, botas e casacos entram no mesmo mod.

**Créditos:** modelo e texturas da Jaqueta S.A.B. por **Novatsugs** (*Novatsugs Extra Clothes*), usados com permissão do criador.

## Instalar

1. Suba esta pasta num repositório do GitHub e rode o workflow **Build** (aba Actions).
2. Baixe o `.jar` do artifact e coloque na pasta `mods`, junto com o DragonMine Z 2.2 e o Curios (que o DMZ já exige).

## Usar

- **Obter:** aba de criativo **DMZ: Kitty's Clothes** (ou `/give @s dmz_kitty_clothes:sab_jacket`).
- **Equipar:** na tela do Curios, slot **Jaqueta**.
- **Tingir:** mesa de crafting, jaqueta + corantes (igual armadura de couro, dá para misturar corantes). Sem tinta ela é azul (textura original); com tinta usa a textura cinza do criador com a cor aplicada.
- **Tirar a tinta:** clique com a jaqueta num caldeirão com água.

## Correção da pose (mixin)

As roupas de Curios são desenhadas sobre um modelo vanilla que o DMZ mantém sincronizado com a animação do personagem (`VanillaModelSync`). Essa cópia é aproximada:

- a posição X do braço sai com o sinal invertido (a roupa cruza o braço para fora quando o personagem cruza para dentro);
- a rotação da cintura é somada à do braço, o que só vale se os dois girarem no mesmo eixo.

O resultado é a roupa fora da pose do corpo: parece trocar de braço, "mexe sozinha" e deixa a camisa aparecer.

Este mod aplica um mixin (`VanillaModelSyncMixin`) que deixa o DMZ rodar e depois sobrescreve as poses de cabeça, corpo, braços e pernas com o cálculo exato (`PoseSolver`: cadeia completa de ossos composta como matrizes, igual ao `prepMatrixForBone` do GeckoLib). Vale para **todas** as roupas deste mod.

- **Comparar no jogo:** inicie com `-Ddmzkitty.exactPose=false` para voltar ao comportamento original do DMZ.
- **Conferir a matemática sem o Minecraft:** na raiz do repositório,
  `javac -d /tmp/pose-check src/main/java/com/dmzkittyclothes/client/pose/PoseSolver.java dev/PoseSolverCheck.java && java -cp /tmp/pose-check PoseSolverCheck`
- Se o DMZ corrigir isso por conta própria, o resultado é o mesmo e o mixin pode ser removido.

## Notas e limitações

- O DMZ não desenha camadas de terceiros em formas Oozaru; as roupas somem nessas formas.
- A escala dos ossos (ex.: formas com corpo maior) não entra no cálculo da pose.
- O modelo da jaqueta foi convertido do Bedrock (`geometry.overcoat`). A camada do peito (`breasts`) fica de fora, como no addon original (ele a esconde para a armadura).
- Tingido: os pixels cinza recebem a cor; os detalhes que o original marca como "não tingíveis" (faixas, punhos, detalhes roxos) são desenhados de novo sem tinta, por uma segunda textura.
- A camisa ainda pode aparecer por baixo se a camada de roupa do DMZ for mais grossa que a jaqueta; isso é diferente do problema de pose e precisa ser visto no jogo.
- Ainda não foi compilado nem testado no jogo.

## v1.1.0

- O mod virou **DMZ: Kitty's Clothes** (ID `dmz_kitty_clothes`), para reunir todas as roupas. **Itens antigos `dmz_sab_jacket:sab_jacket` deixam de existir:** pegue a jaqueta de novo com `/give @s dmz_kitty_clothes:sab_jacket`.
- Novo mixin que corrige a pose das roupas nas animações do DMZ.

## v1.0.1

- Removido o peito (seios) do modelo.
- Tingimento: os detalhes que não são tingíveis voltam a aparecer, no corpo e no ícone.
- Manga direita com a mesma espessura da esquerda (a original tinha 0,21 contra 0,3).

## Cores da jaqueta (v1.3.0)

Tres regioes, cada uma com seu corante, na bancada de trabalho:

| Regiao | Receita |
|---|---|
| Principal | jaqueta + corante(s) (receita normal de armadura de couro) |
| Logo | jaqueta + corante(s) + Kit de Coloração 2 |
| Listra | jaqueta + corante(s) + Kit de Coloração 3 |

Kit 2 = la branca + corante branco (ou farinha de osso). Kit 3 = la preta + corante preto (ou tinta de lula). O kit NAO e consumido (volta para a bancada).

Regiao sem corante mantem a cor original da textura. Agua no caldeirao remove as tres cores.
Os marcadores sao as tags `dmz_kitty_clothes:kit_2_marker` e `dmz_kitty_clothes:kit_3_marker` (trocaveis por datapack).
Texturas: `sab_jacket.png` (original completa), `sab_jacket_dyed.png` (principal), `sab_jacket_dyed_color2.png` (logo), `sab_jacket_dyed_color3.png` (listra); as tres em tons de cinza, tingidas por cima da original.

## Luvas (v1.4.0)

Slot Curios `gloves`. Quatro itens: Luvas, Luvas sem Dedos, Luvas Longas, Luvas Longas sem Dedos. Botao direito equipa; tingem com a receita de armadura de couro; agua no caldeirao remove a cor.
Texturas em `textures/models/`: `<nome>.png` (aparencia padrao) e `<nome>_dyed.png` (tons de cinza, tingida). No UV de skin 64x64 (braco direito em 40,16 e esquerdo em 32,48).

## Macacões (v1.5.0)

Slot Curios `bodysuit`; oito itens (`jumpsuit*`), tingem com corante (receita de armadura de couro), água no caldeirao remove, botão direito equipa. Espessura 0.15 (config `[suit]`), por baixo das luvas (0.2) e da jaqueta (0.33+).

## Cores exatas e contraste (v1.5.1)

- `/kittycolor <main|second|third> <hex>` (precisa de cheats): define a cor exata do item na mão principal (ex.: `/kittycolor main #1B2A6B`); `second`/`third` só na jaqueta. `/kittycolor clear` remove as cores.
- Texturas tingidas têm o sombreado realçado automaticamente; ajuste em `[tint] contrastFloor` na config.

Cores escuras (v1.5.2): a textura tingida é gerada por cor, somando um brilho que acompanha o sombreado, então preto/azul-marinho mantêm os detalhes. Ajuste em `[tint] darkDetail` (0 desliga).

## Colete e faixas (v1.6.0)

Slots Curios `vest` e `waist`. Colete Saiyajin Básico (cor principal + Kit 2) e quatro faixas (Faixa, com Pontas, com Ponta Esquerda, com Ponta Direita). Espessuras em `[accessories]` (acima do macacão). A jaqueta não usa mais o realce de detalhe (`[tint]`).
