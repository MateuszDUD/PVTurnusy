/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pv.alg.lp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import pv.alg.bean.Spoj2;
import pv.alg.others.NodesConnectionsCreator;
import pv.bean.Spoj;

/**
 *
 * @author Ja
 */
public class LpTurnusMinTGarage {

    private final static String LP_MODEL = "src/main/resources/lp/LpTurnusMinTGarage.txt";
    private final int MAX_C = 400;

    private Map<Integer, Map<Integer, Integer>> distances;
    private List<Spoj> spojeSimple;
    private List<Spoj2> spoje2;
    private Integer garageId;

    public void solve(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje, Integer garageId) {
        createModel(distances, spoje, garageId);

        LpSolver lp = new LpSolver();
        lp.solve(LP_MODEL);
    }

    public void createModel(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje, Integer garageId) {
        this.distances = distances;
        this.spojeSimple = spoje;
        this.spoje2 = NodesConnectionsCreator.createPossibleConnections(spojeSimple, distances);
        this.garageId = garageId;

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(LP_MODEL), "utf-8"))) {

            createLinearFunction(writer);
            createSubject(writer);
            writer.write("END");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                Integer distance = distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());
                s += distance == 0
                        ? 1
                        : distance;
                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
//                s += "*" + distances.get(spoje2.get(i).getToId()).get(spoje2.get(j).getFromId());

                if (s.length() > MAX_C) {
                    writer.write(s);
                    writer.write("\n");
                    s = " ";
                }
            }
        }
        s += "+";
        Map<Integer, Integer> distancesFromGarage = distances.get(garageId);
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
                if (s.length() > MAX_C) {
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
            Integer distance = distances.get(spoje2.get(i).getToId()).get(garageId);
            s += distance == 0
                    ? 1
                    : distance;
            s += "v" + spoje2.get(i).getId();
            if (s.length() > MAX_C) {
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

                if (s.length() > MAX_C) {
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
}
