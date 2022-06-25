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
 * autobusov s tym, ze kazdy spoj zacina a konci v garazi.
 */
public class LpModelMinTGaraz extends LpModelWriter {

    private final int idGaraz;

    public LpModelMinTGaraz(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spojeSimple, int idGaraz) {
        super(distances, spojeSimple, "LpTurnusMinTGaraz");
        this.idGaraz = idGaraz;
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

    // je potrebne vytvorit dve nove premenne - u_j = 1, ak spoj j je prvym spojom
    // turnusu a v_i = 1, ak spoj i je poslednym spojom turnusu
    // do ucelovej funkcie z minimalizacie prazdnych prejazdov sa tak doplni suma
    // u_j vynasobena vzdialenostou medzi garazou a miestom odchodu spoja j a 
    // suma v_i vynasobena vzdialenostou medzi miestom prichodu spoja i a garazou
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
        s += "+";
        Map<Integer, Integer> distancesFromGarage = distances.get(idGaraz);
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {
                if (!s.equals(" ")) {
                    s += "+";
                }
                Integer distance = distancesFromGarage.get(spoje2.get(j).getFromId());
                s += distance == 0
                        ? 1
                        : distance;
                s += "u" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = " ";
                }
            }
        }
        s += "+";
        for (int i = 0; i < spoje2.size(); i++) {
            if (!s.equals(" ")) {
                s += "+";
            }
            Integer distance = distances.get(spoje2.get(i).getToId()).get(idGaraz);
            s += distance == 0
                    ? 1
                    : distance;
            s += "v" + spoje2.get(i).getId();
            if (s.length() > max_c) {
                writer.write(s);
                writer.write("\n");
                s = " ";
            }
        }
        if (!s.equals(" ")) {
            writer.write(s);
            writer.write("\n\n");
        }
    }

    // podmienky
    private void createSubject(Writer writer) throws IOException {
        writer.write("Subject To\n");

        int cNumber = 0;

        for (int i = 0; i < spoje2.size(); i++) {
            if (spoje2.get(i).getPossibleConnectionsFromThis().isEmpty()) {
                continue;
            }

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";

            int j = 0;
            s += "u" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
            while (j < spoje2.get(i).getPossibleConnectionsFromThis().size()) {
                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > max_c) {
                    writer.write(s);
                    writer.write("\n");
                    s = "  ";
                }
                j++;
            }

            if (!s.equals(" ")) {
                writer.write(s);
                writer.write("\n");
            }
            writer.write("  = 1\n");
        }

        for (int i = 0; i < spoje2.size(); i++) {
            if (spoje2.get(i).getPossibleConnectionsToThis().isEmpty()) {
                continue;
            }

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";
            s += "v" + spoje2.get(i).getId();

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
            writer.write("  = 1\n");
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
