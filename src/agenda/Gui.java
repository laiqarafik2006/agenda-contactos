package agenda;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

public class Gui {

    private static final String FICHEIRO_PADRAO = "data/contactos.csv";
    private static final String FICHEIRO_ALTERNATIVO = "agenda-contactos/data/contactos.csv";

    public static void main(String[] args) {
        String caminho = escolherFicheiro(args);
        Agenda agenda = new Agenda(caminho);
        Path completo = Paths.get(caminho).toAbsolutePath();

        String resumo;
        boolean erro = false;
        try {
            int[] r = agenda.carregar();
            if (!Files.exists(completo)) {
                resumo = "Ficheiro não encontrado (será criado ao adicionar um contacto): " + completo;
                erro = true;
            } else {
                resumo = r[1] + " contacto(s) carregado(s) de " + completo
                        + (r[2] > 0 ? " (" + r[2] + " linha(s) ignorada(s))." : ".");
            }
        } catch (IOException e) {
            resumo = "Não foi possível ler o ficheiro: " + e.getMessage();
            erro = true;
        }
        final String mensagem = resumo;
        final boolean mensagemErro = erro;

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {

            }
            aumentarFontes(14f);

            PainelAgenda painel = new PainelAgenda(agenda);
            painel.setEstado(mensagem, mensagemErro);

            JFrame janela = new JFrame("Agenda Electrónica de Contactos");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(painel);
            janela.setSize(920, 640);
            janela.setMinimumSize(new Dimension(780, 540));
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }

    static String escolherFicheiro(String[] args) {
        if (args.length > 0) return args[0];
        if (Files.exists(Paths.get(FICHEIRO_PADRAO))) return FICHEIRO_PADRAO;
        if (Files.exists(Paths.get(FICHEIRO_ALTERNATIVO))) return FICHEIRO_ALTERNATIVO;
        return FICHEIRO_PADRAO;
    }

    public static void aumentarFontes(float tamanho) {
        Enumeration<Object> chaves = UIManager.getDefaults().keys();
        while (chaves.hasMoreElements()) {
            Object chave = chaves.nextElement();
            Object valor = UIManager.get(chave);
            if (valor instanceof FontUIResource) {
                Font f = (FontUIResource) valor;
                UIManager.put(chave, new FontUIResource(f.deriveFont(tamanho)));
            }
        }
    }
}