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
 * Trieda vytvori a zapise do suboru model pre minimalizaciu prazdnych prejazdov
 * autobusov.
 */
public class LpModelMinT extends LpModelWriter {

    public LpModelMinT(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spojeSimple) {
        super(distances, spojeSimple, "LpTurnusMinT.txt");
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

    // ucelova funkcia je minimalizacia suctu x_ij premennych vynasobenych
    // vzdialenostou medzi miestom prichodu spoja i a odchodu spoja j
    private void createLinearFunction(Writer writer) throws IOException {
        writer.write("Minimize\n");
        writer.write(" obj:\n");

        String s = " ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }
                Integer distance = distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());
                s += distance == 0
                        ? 1
                        : distance;
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

    // podmienky su rovnake ako pri zistovani minimalneho poctu autobusov, len
    // je treba pridat este jednu, a to ze suma x_ij je rovna rieseniu modelu
    // poctu autobusov
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

        writer.write(" c" + cNumber++ + ":\n");
        String s = "  ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals("  ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = "  ";
                }
            }
        }
        if (!s.equals("  ")) {
            writer.write(s);
            writer.write("\n");
        }
        writer.write("  = 777");

        writer.write("\n");
    }

}
