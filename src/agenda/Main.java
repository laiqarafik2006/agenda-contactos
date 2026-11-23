package agenda;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;


public class Main {

    private static final String FICHEIRO_PADRAO = "data/contactos.csv";

    private static Agenda agenda;
    private static Scanner in;

    public static void main(String[] args) {
        String caminho = args.length > 0 ? args[0] : FICHEIRO_PADRAO;
        agenda = new Agenda(caminho);

        System.setOut(new PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        in = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8);

        try {
            int[] r = agenda.carregar();
            System.out.println("Ficheiro '" + caminho + "': " + r[0] + " linhas lidas, "
                    + r[1] + " contactos carregados, " + r[2] + " invalidos/duplicados.");
        } catch (IOException e) {
            System.out.println("Nao foi possivel ler o ficheiro: " + e.getMessage());
        }

        String opcao;
        do {
            mostrarMenu();
            opcao = ler("Opcao: ");
            if (opcao == null) break;
            switch (opcao) {
                case "1": adicionar(); break;
                case "2": pesquisarNome(); break;
                case "3": editar(); break;
                case "4": remover(); break;
                case "5": listar(); break;
                case "6": pesquisarPrefixo(); break;
                case "7": pesquisarTelefone(); break;
                case "8": estatisticas(); break;
                case "9": agenda.getTabela().mostrarTabela(); break;
                case "0": System.out.println("Programa terminado."); break;
                default: System.out.println("Opcao invalida.");
            }
        } while (!"0".equals(opcao));
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("================================================");
        System.out.println("        AGENDA ELECTRONICA DE CONTACTOS");
        System.out.println("================================================");
        System.out.println("1 - Adicionar contacto");
        System.out.println("2 - Pesquisar por nome");
        System.out.println("3 - Editar contacto");
        System.out.println("4 - Remover contacto");
        System.out.println("5 - Listar todos (ordem alfabetica)");
        System.out.println("6 - Pesquisar pelo inicio do nome");
        System.out.println("7 - Pesquisar por telefone");
        System.out.println("8 - Estatisticas da Tabela Hash");
        System.out.println("9 - Mostrar a Tabela Hash por dentro");
        System.out.println("0 - Sair");
    }


    private static void adicionar() {
        String nome = ler("Nome: ");
        String tel = ler("Telefone: ");
        String email = ler("Email (opcional): ");
        String erro = agenda.adicionar(nome, tel, email);
        if (erro != null) {
            System.out.println("Erro: " + erro);
        } else {
            System.out.println("Contacto adicionado.");
            gravar();
        }
    }

    private static void pesquisarNome() {
        Contacto c = agenda.pesquisar(ler("Nome: "));
        if (c == null) {
            System.out.println("Contacto nao encontrado.");
        } else {
            mostrarCabecalho();
            System.out.println(c);
        }
    }

    private static void editar() {
        String nome = ler("Nome do contacto a editar: ");
        Contacto c = agenda.pesquisar(nome);
        if (c == null) {
            System.out.println("Contacto nao encontrado.");
            return;
        }
        System.out.println("Atual: " + c);
        System.out.println("(deixe em branco para manter o valor atual)");
        String novoNome = ler("Novo nome: ");
        String tel = ler("Novo telefone: ");
        String email = ler("Novo email: ");
        String erro = agenda.editar(nome, novoNome, tel, email);
        if (erro != null) {
            System.out.println("Erro: " + erro);
        } else {
            System.out.println("Contacto atualizado.");
            gravar();
        }
    }

    private static void remover() {
        String nome = ler("Nome do contacto a remover: ");
        if (agenda.remover(nome)) {
            System.out.println("Contacto removido.");
            gravar();
        } else {
            System.out.println("Contacto nao encontrado.");
        }
    }

    private static void listar() {
        Contacto[] v = agenda.listarOrdenado();
        if (v.length == 0) {
            System.out.println("A agenda esta vazia.");
            return;
        }
        mostrarCabecalho();
        for (int i = 0; i < v.length; i++) System.out.println(v[i]);
        System.out.println("Total: " + v.length + " contactos.");
    }

    private static void pesquisarPrefixo() {
        Contacto[] v = agenda.pesquisarPorPrefixo(ler("Inicio do nome: "));
        if (v.length == 0) {
            System.out.println("Nenhum contacto encontrado.");
            return;
        }
        mostrarCabecalho();
        for (int i = 0; i < v.length; i++) System.out.println(v[i]);
    }

    private static void pesquisarTelefone() {
        Contacto c = agenda.pesquisarPorTelefone(ler("Telefone: "));
        if (c == null) {
            System.out.println("Nenhum contacto com esse telefone.");
        } else {
            mostrarCabecalho();
            System.out.println(c);
        }
    }

    private static void estatisticas() {
        TabelaHash t = agenda.getTabela();
        System.out.println("========== ESTATISTICAS DA TABELA HASH ==========");
        System.out.println("Capacidade inicial     : " + t.capacidadeInicial());
        System.out.println("Capacidade atual       : " + t.capacidade());
        System.out.println("Contactos armazenados  : " + t.tamanho());
        System.out.printf ("Fator de carga         : %.2f%n", t.fatorCarga());
        System.out.println("Posicoes ocupadas      : " + t.bucketsOcupados());
        System.out.println("Colisoes               : " + t.colisoes());
        System.out.println("Maior lista (bucket)   : " + t.maiorBucket());
        System.out.println("Redimensionamentos     : " + t.redimensionamentos());
    }


    private static void mostrarCabecalho() {
        System.out.println(String.format("%-25s | %-15s | %s", "NOME", "TELEFONE", "EMAIL"));
        System.out.println("-------------------------------------------------------------");
    }

    private static void gravar() {
        try {
            agenda.guardar();
        } catch (IOException e) {
            System.out.println("Aviso: nao foi possivel gravar o ficheiro: " + e.getMessage());
        }
    }

    private static String ler(String texto) {
        System.out.print(texto);
        if (!in.hasNextLine()) return null;
        return in.nextLine().trim();
    }
}
