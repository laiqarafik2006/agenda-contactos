package agenda;

public class ListaLigada {

    public static class No {
        Contacto dado;
        No proximo;

        No(Contacto dado) {
            this.dado = dado;
        }

        public Contacto getDado() { return dado; }
        public No getProximo() { return proximo; }
    }

    private No primeiro;
    private int tamanho;

    public ListaLigada() {
        this.primeiro = null;
        this.tamanho = 0;
    }

    public No getPrimeiro() { return primeiro; }
    public int getTamanho() { return tamanho; }
    public boolean estaVazia() { return primeiro == null; }

    public void inserirInicio(Contacto c) {
        No novo = new No(c);
        novo.proximo = primeiro;
        primeiro = novo;
        tamanho++;
    }

    public Contacto buscar(String chave) {
        No atual = primeiro;
        while (atual != null) {
            if (atual.dado.getChave().equals(chave)) {
                return atual.dado;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public boolean remover(String chave) {
        No atual = primeiro;
        No anterior = null;
        while (atual != null) {
            if (atual.dado.getChave().equals(chave)) {
                if (anterior == null) {
                    primeiro = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }
                tamanho--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }
}
