# Agenda Electrónica de Contactos

Projecto semestral de **Estruturas de Dados e Algoritmos** — Engenharia Informática, Faculdade de Engenharia, UEM.
Projecto nº 4: *Agenda electrónica que permite inserir, pesquisar, editar e eliminar contactos rapidamente.*

**Estruturas de dados aplicadas:** Tabela Hash + Lista Ligada (ambas implementadas de raiz, sem `HashMap`, `LinkedList` ou outras colecções do Java para guardar os contactos).

## Como funciona

| Ficheiro | O que faz | Matéria da aula |
|----------|-----------|-----------------|
| `Contacto.java` | Nome, telefone e email; normaliza o nome (chave) e valida os dados | TAD / validação |
| `ListaLigada.java` | Lista ligada simples; cada posição da tabela é uma lista | Lista Ligada (Lab 1) |
| `TabelaHash.java` | Função hash polinomial, encadeamento separado, fator de carga e rehashing | Tabelas Hash (Lab 3) |
| `Agenda.java` | Inserir, pesquisar, editar, remover, listar, ficheiro CSV | Aplicação das estruturas |
| `Main.java` | Menu de texto | — |

- **Chave:** o nome do contacto, sem acentos, em minúsculas e sem espaços a mais (`"João  Silva"` = `"joao silva"`).
- **Função hash:** `h = 31*h + caractere`, depois `índice = h mod m`.
- **Colisões:** encadeamento separado — contactos com o mesmo índice ficam na mesma lista ligada.
- **Fator de carga:** `α = n / m`. Quando `α > 0,75` a tabela cresce para o primeiro número primo ≥ `2m` e **todos** os contactos são reinseridos pela função hash (rehashing).
- **Dados:** gravados em `data/contactos.csv` (formato `nome;telefone;email`) sempre que há alterações.

### Complexidade

| Operação | Média | Pior caso |
|----------|-------|-----------|
| Inserir | O(1) | O(n) |
| Pesquisar por nome | O(1) | O(n) |
| Editar (sem mudar o nome) | O(1) | O(n) |
| Remover | O(1) | O(n) |
| Listar ordenado / pesquisar por início do nome / por telefone | O(n) (a ordenação por inserção é O(n²)) | — |

A pesquisa por telefone e por início do nome percorre toda a tabela, porque a chave é o nome.

## Como executar

Só é preciso ter o **JDK 11 ou superior** instalado (`javac -version` para confirmar).

```
git clone <URL-DO-REPOSITORIO>
cd agenda-contactos
```

- **Windows:** `run.bat`
- **Linux / macOS:** `./run.sh`

Sem os scripts (qualquer sistema), a partir da pasta do projecto:

```
javac -encoding UTF-8 -d out src/agenda/*.java
java -cp out agenda.Main
```

Importante: executar sempre a partir da pasta do projecto, para o programa encontrar `data/contactos.csv`.
Também funciona noutro ficheiro: `java -cp out agenda.Main caminho/outro.csv`.
