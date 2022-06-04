package pv.alg.lp;

import pv.alg.bean.Spoj2;
import pv.alg.others.NodesConnectionsCreator;
import pv.bean.Spoj;
import pv.config.GlobalConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LpTurnusMinT {

    private final static String LP_MODEL = "src/main/resources/lp/LpTurnusMinT.txt";
    private final int MAX_C = 400;

    private Map<Integer, Map<Integer, Integer>> distances;
    private List<Spoj> spojeSimple;
    private List<Spoj2> spoje2;

    public void solve(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje) {
        createModel(distances, spoje);

        LpSolver lp = new LpSolver();
        lp.solve(LP_MODEL);
    }

    public void createModel(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje) {
        this.distances = distances;
        this.spojeSimple = spoje;
        this.spoje2 = NodesConnectionsCreator.createPossibleConnections(spojeSimple, distances);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(LP_MODEL), "utf-8"))) {

            createLinearFunction(writer);
            createSubject(writer);
//            createBounds(writer);
//            createBinaries(writer);
            writer.write("END");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSubject(Writer writer) throws IOException {
        writer.write("Subject To\n");

        int cNumber = 0;

        for (int i = 0; i < spoje2.size(); i++) {
            if (spoje2.get(i).getPossibleConnectionsFromThis().isEmpty())
                continue;

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";

            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > MAX_C) {
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
            if (spoje2.get(i).getPossibleConnectionsToThis().isEmpty())
                continue;

            writer.write(" c" + cNumber++ + ":\n");
            String s = "  ";

            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsToThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }

                s += "x" + spoje2.get(i).getPossibleConnectionsToThis().get(j).getId() + "_" + spoje2.get(i).getId();

                if (s.length() > MAX_C) {
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

                if (s.length() > MAX_C) {
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

    private void createBinaries(Writer writer) throws IOException {
        writer.write("Binaries\n");
        String s = " ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += " ";
                }

                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();

                if (s.length() > MAX_C) {
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

    private void createBounds(Writer writer) throws IOException {
        writer.write("Bounds\n");

        String s = " ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                s = " c" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
                s += " = " + distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());
                s += "\n";
                writer.write(s);
            }
        }
    }

    private void createLinearFunction(Writer writer) throws IOException {
        writer.write("Minimize\n");
        writer.write(" obj:\n");

        String s = " ";
        for (int i = 0; i < spoje2.size(); i++) {
            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {

                if (!s.equals(" ")) {
                    s += "+";
                }
                s += distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId()) == 0 ? 1 :
                        distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());
                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
//                s += "*" + distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());

                if (s.length() > MAX_C) {
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
}
