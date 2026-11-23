package agenda;

import java.text.Normalizer;


public class Contacto {
    private String nome;
    private String telefone;
    private String email;

    public Contacto(String nome, String telefone, String email) {
        this.nome = limpar(nome);
        this.telefone = limpar(telefone);
        this.email = limpar(email);
    }

    public static String normalizar(String nome) {
        if (nome == null) return "";
        String s = Normalizer.normalize(nome.trim(), Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = s.replaceAll("\\s+", " ");
        return s.toLowerCase();
    }

    public String getChave() {
        return normalizar(nome);
    }

    public static boolean nomeValido(String nome) {
        return nome != null && !nome.trim().isEmpty();
    }

    public static boolean telefoneValido(String tel) {
        return tel != null && tel.trim().matches("\\+?\\d{9,15}");
    }

    public static boolean emailValido(String email) {
        return email == null || email.trim().isEmpty()
                || email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }

    private static String limpar(String s) {
        return s == null ? "" : s.trim().replace(";", ",");
    }

    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public void setTelefone(String telefone) { this.telefone = limpar(telefone); }
    public void setEmail(String email) { this.email = limpar(email); }

    @Override
    public String toString() {
        return String.format("%-25s | %-15s | %s", nome, telefone, email.isEmpty() ? "-" : email);
    }

    public String paraCsv() {
        return nome + ";" + telefone + ";" + email;
    }
}
