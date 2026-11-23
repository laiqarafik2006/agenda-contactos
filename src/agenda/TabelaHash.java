package agenda;


public class TabelaHash {

    private static final double FATOR_CARGA_MAXIMO = 0.75;

    private ListaLigada[] tabela;
    private int tamanho;

    private int colisoes;
    private int redimensionamentos;
    private final int capacidadeInicial;

    public TabelaHash(int capacidade) {
        this.capacidadeInicial = capacidade;
        this.tabela = criarTabela(capacidade);
        this.tamanho = 0;
    }

    private static ListaLigada[] criarTabela(int capacidade) {
        ListaLigada[] t = new ListaLigada[capacidade];
        for (int i = 0; i < capacidade; i++) {
            t[i] = new ListaLigada();
        }
        return t;
    }

    private int hash(String chave, int capacidade) {
        int h = 0;
        for (int i = 0; i < chave.length(); i++) {
            h = 31 * h + chave.charAt(i);   // hash polinomial
        }
        return Math.floorMod(h, capacidade); // garante indice entre 0 e m-1
    }

    private int hash(String chave) {
        return hash(chave, tabela.length);
    }


    public boolean inserir(Contacto c) {
        String chave = c.getChave();
        int indice = hash(chave);

        if (tabela[indice].buscar(chave) != null) {
            return false;
        }
        if (!tabela[indice].estaVazia()) {
            colisoes++;
        }
        tabela[indice].inserirInicio(c);
        tamanho++;

        if (fatorCarga() > FATOR_CARGA_MAXIMO) {
            redimensionar();
        }
        return true;
    }

    public Contacto buscar(String nome) {
        String chave = Contacto.normalizar(nome);
        return tabela[hash(chave)].buscar(chave);
    }

    public boolean contem(String nome) {
        return buscar(nome) != null;
    }

    public boolean remover(String nome) {
        String chave = Contacto.normalizar(nome);
        boolean removeu = tabela[hash(chave)].remover(chave);
        if (removeu) {
            tamanho--;
        }
        return removeu;
    }

    private void redimensionar() {
        int novaCapacidade = proximoPrimo(tabela.length * 2);
        ListaLigada[] antiga = tabela;
        tabela = criarTabela(novaCapacidade);

        for (int i = 0; i < antiga.length; i++) {
            ListaLigada.No atual = antiga[i].getPrimeiro();
            while (atual != null) {
                int novoIndice = hash(atual.getDado().getChave());
                tabela[novoIndice].inserirInicio(atual.getDado());
                atual = atual.getProximo();
            }
        }
        redimensionamentos++;
    }

    private static int proximoPrimo(int n) {
        while (!ePrimo(n)) {
            n++;
        }
        return n;
    }

    private static boolean ePrimo(int n) {
        if (n < 2) return false;
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }


    public Contacto[] todos() {
        Contacto[] resultado = new Contacto[tamanho];
        int k = 0;
        for (int i = 0; i < tabela.length; i++) {
            ListaLigada.No atual = tabela[i].getPrimeiro();
            while (atual != null) {
                resultado[k++] = atual.getDado();
                atual = atual.getProximo();
            }
        }
        return resultado;
    }

    public int tamanho() { return tamanho; }
    public boolean estaVazia() { return tamanho == 0; }
    public int capacidade() { return tabela.length; }
    public int capacidadeInicial() { return capacidadeInicial; }
    public double fatorCarga() { return (double) tamanho / tabela.length; }
    public int colisoes() { return colisoes; }
    public int redimensionamentos() { return redimensionamentos; }

    public int maiorBucket() {
        int maior = 0;
        for (int i = 0; i < tabela.length; i++) {
            if (tabela[i].getTamanho() > maior) maior = tabela[i].getTamanho();
        }
        return maior;
    }

    public int bucketsOcupados() {
        int n = 0;
        for (int i = 0; i < tabela.length; i++) {
            if (!tabela[i].estaVazia()) n++;
        }
        return n;
    }

    public String descrever(boolean apenasOcupadas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabela.length; i++) {
            if (apenasOcupadas && tabela[i].estaVazia()) continue;
            sb.append(i).append(": ");
            ListaLigada.No atual = tabela[i].getPrimeiro();
            while (atual != null) {
                sb.append("[").append(atual.getDado().getNome()).append("] -> ");
                atual = atual.getProximo();
            }
            sb.append("null\n");
        }
        return sb.toString();
    }

    public void mostrarTabela() {
        System.out.print(descrever(false));
    }

    public int indiceDe(String nome) {
        return hash(Contacto.normalizar(nome));
    }
}
