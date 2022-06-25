package pv.alg.lp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import pv.bean.Spoj;

/**
 * Trieda vytvori a zapise do suboru model pre zistenie najmensieho poctu
 * autobusov potrebnych na obsluzenie vsetkych spojov.
 */
public class LpModelBus extends LpModelWriter {

    public LpModelBus(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spojeSimple) {
        super(distances, spojeSimple, "LpTurnusModel.txt");
    }

    @Override
    public void createModel() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(lp_model), "utf-8"))) {
            createLinearFunction(writer);
            createSubject(writer);
            writer.write("END");
            writer.close();
        } catch (Exception e) {
        }
    }

    // premenna x_ij znaci, ci spoj j bude bezprostredne nasledovat za spojom i,
    // vtedy ma premenna hodnotu 1
    // pre zistenie minimalneho poctu autobusov maximalizujeme sumu x_ij, ktoru
    // na ziskanie vysledku treba odcitat od poctu vsetkych spojov
    private void createLinearFunction(Writer writer) throws IOException {
        writer.write("Maximize\n");
        writer.write(" obj:\n");
        String s = " ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = " ";
                }
            }
        }
        if (!s.equals(" ")) {
            writer.write(s);
            writer.write("\n\n");
        }
    }

    // priradovacie podmienky
    private void createSubject(Writer writer) throws IOException {
        writer.write("Subject To\n");

        int cNumber = 0;

        for (int i = 0; i < spoje2.size(); i++) {
            if (spoje2.get(i).getPossibleConnectionsFromThis().isEmpty()) {
                continue;
            }

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";

            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = "  ";
                }
            }
            if (!s.equals(" ")) {
                writer.write(s);
                writer.write("\n");
            }
            writer.write("  <= 1\n");
        }

        for (int i = 0; i < spoje2.size(); i++) {
            if (spoje2.get(i).getPossibleConnectionsToThis().isEmpty()) {
                continue;
            }

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";

            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsToThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getPossibleConnectionsToThis().get(j).getId() + "_" + spoje2.get(i).getId();

                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = "  ";
                }
            }
            if (!s.equals(" ")) {
                writer.write(s);
                writer.write("\n");
            }
            writer.write("  <= 1\n");
        }

        writer.write("\n");
    }

}
