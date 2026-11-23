package agenda;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Agenda {

    private static final int CAPACIDADE_INICIAL = 11;

    private final TabelaHash tabela;
    private final Path ficheiro;

    public Agenda(String caminhoFicheiro) {
        this.tabela = new TabelaHash(CAPACIDADE_INICIAL);
        this.ficheiro = Paths.get(caminhoFicheiro);
    }

    public TabelaHash getTabela() { return tabela; }


    public String adicionar(String nome, String telefone, String email) {
        String erro = validar(nome, telefone, email);
        if (erro != null) return erro;
        if (!tabela.inserir(new Contacto(nome, telefone, email))) {
            return "Ja existe um contacto com o nome '" + nome.trim() + "'.";
        }
        return null;
    }


    public Contacto pesquisar(String nome) {
        return tabela.buscar(nome);
    }


    public String editar(String nomeAtual, String novoNome, String novoTelefone, String novoEmail) {
        Contacto atual = tabela.buscar(nomeAtual);
        if (atual == null) return "Contacto nao encontrado.";

        String nome = vazio(novoNome) ? atual.getNome() : novoNome.trim();
        String tel = vazio(novoTelefone) ? atual.getTelefone() : novoTelefone.trim();
        String email = vazio(novoEmail) ? atual.getEmail() : novoEmail.trim();

        String erro = validar(nome, tel, email);
        if (erro != null) return erro;

        boolean mesmaChave = Contacto.normalizar(nome).equals(atual.getChave());
        if (mesmaChave) {
            // a chave nao muda: basta alterar os dados no proprio no
            atual.setTelefone(tel);
            atual.setEmail(email);
            return null;
        }
        if (tabela.contem(nome)) {
            return "Ja existe outro contacto com o nome '" + nome + "'.";
        }
        tabela.remover(atual.getNome());
        tabela.inserir(new Contacto(nome, tel, email));
        return null;
    }

    public boolean remover(String nome) {
        return tabela.remover(nome);
    }


    public Contacto[] listarOrdenado() {
        Contacto[] v = tabela.todos();
        ordenar(v);
        return v;
    }

    public Contacto[] pesquisarPorPrefixo(String prefixo) {
        String p = Contacto.normalizar(prefixo);
        Contacto[] todos = tabela.todos();
        int n = 0;                                   // 1.a passagem: contar
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getChave().startsWith(p)) n++;
        }
        Contacto[] v = new Contacto[n];              // 2.a passagem: copiar
        int k = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getChave().startsWith(p)) v[k++] = todos[i];
        }
        ordenar(v);
        return v;
    }

    public Contacto pesquisarPorTelefone(String telefone) {
        Contacto[] todos = tabela.todos();
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getTelefone().equals(telefone.trim())) return todos[i];
        }
        return null;
    }

    private static void ordenar(Contacto[] v) {
        for (int i = 1; i < v.length; i++) {
            Contacto x = v[i];
            int j = i - 1;
            while (j >= 0 && v[j].getChave().compareTo(x.getChave()) > 0) {
                v[j + 1] = v[j];
                j--;
            }
            v[j + 1] = x;
        }
    }

    public int[] carregar() throws IOException {
        int lidos = 0, inseridos = 0, invalidos = 0;
        if (!Files.exists(ficheiro)) {
            return new int[] {0, 0, 0};
        }
        List<String> linhas = Files.readAllLines(ficheiro, StandardCharsets.UTF_8);
        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) continue;
            if (i == 0 && linha.toLowerCase().startsWith("nome;")) continue; // cabecalho
            lidos++;
            String[] campos = linha.split(";", -1);
            if (campos.length < 2) {
                invalidos++;
                continue;
            }
            String email = campos.length > 2 ? campos[2] : "";
            if (adicionar(campos[0], campos[1], email) == null) {
                inseridos++;
            } else {
                invalidos++;   // dados invalidos ou nome duplicado
            }
        }
        return new int[] {lidos, inseridos, invalidos};
    }

    public void guardar() throws IOException {
        Path pasta = ficheiro.toAbsolutePath().getParent();
        if (pasta != null) Files.createDirectories(pasta);
        List<String> linhas = new ArrayList<>();
        linhas.add("nome;telefone;email");
        Contacto[] v = listarOrdenado();
        for (int i = 0; i < v.length; i++) {
            linhas.add(v[i].paraCsv());
        }
        Files.write(ficheiro, linhas, StandardCharsets.UTF_8);
    }

    private static String validar(String nome, String telefone, String email) {
        if (!Contacto.nomeValido(nome)) return "O nome nao pode estar vazio.";
        if (!Contacto.telefoneValido(telefone))
            return "Telefone invalido (use 9 a 15 digitos, com '+' opcional).";
        if (!Contacto.emailValido(email)) return "Email invalido.";
        return null;
    }

    private static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
