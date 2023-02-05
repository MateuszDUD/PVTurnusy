package pv.alg.gurobimodel;

import gurobi.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import pv.alg.bean.Spoj2;
import pv.alg.lp.LpSolver;
import pv.alg.others.NodesConnectionsCreator;
import pv.alg.others.RandomTriangularTimeEnricher;
import pv.bean.Spoj;
import pv.config.GlobalConfig;

import java.io.*;
import java.util.*;

public class LpTurnusFuzzy {

    private final RandomTriangularTimeEnricher randomTriangularTimeEnricher = new RandomTriangularTimeEnricher(1L);

    private Map<Integer,Map<Integer, Triple<Long, Long, Long>>> distances;
    private List<Spoj> spojeSimple;
    private List<Spoj2> spoje2;

    private GRBModel model;

    private Map<Integer, Map<Integer, GRBVar>> modelVariables;

    private int c_forName = 1;

    private ModelMode modelMode;

    @Getter
    private double obj_val= -1;

    public LpTurnusFuzzy createModel(List<Spoj2> possibleConnections, Map<Integer,Map<Integer, Triple<Long, Long, Long>>> dist, ModelMode modelMode) {
        spoje2 = possibleConnections;
        modelVariables = new HashMap<>();
        distances = dist;
        this.modelMode = modelMode;

        try {
            model = new GRBModel(new GRBEnv());
        } catch (GRBException e) {
            e.printStackTrace();
        }

        return this;
    }

    @SneakyThrows
    public void solveModel() {
        createVars();
        createObjFunction();
        createConstr();
        model.optimize();

        System.out.println(model.get(GRB.DoubleAttr.ObjVal));
        obj_val = model.get(GRB.DoubleAttr.ObjVal);
    }

    private void createConstr() throws GRBException {
        createConij();
        createConji();

        createConjiLength();
    }

    private void createConjiLength() throws GRBException {
        List<GRBLinExpr> consList = new ArrayList<>();
        List<Integer> startTime = new ArrayList<>();

        for (Spoj2 iSpoj : spoje2) {

            if (!iSpoj.getPossibleConnectionsToThis().isEmpty()) {
                GRBLinExpr cons = new GRBLinExpr();


                for (Spoj2 jSpoj : iSpoj.getPossibleConnectionsToThis()) {
                    int coef = 0;
                    coef += jSpoj.getDeparture().toSecondOfDay();

                    if (modelMode == ModelMode.PESSIMISTIC) {
                        coef += jSpoj.getTriangularTimeDurationSec().getRight();
                        coef += distances.get(jSpoj.getToId()).get(iSpoj.getFromId()).getRight();
                    } else {
                        coef += jSpoj.getTriangularTimeDurationSec().getLeft();
                        coef += distances.get(jSpoj.getToId()).get(iSpoj.getFromId()).getLeft();
                    }

                    coef += GlobalConfig.M;

                    cons.addTerm(coef, modelVariables.get(jSpoj.getId()).get(iSpoj.getId()));
                }
                consList.add(cons);
                startTime.add(iSpoj.getDeparture().toSecondOfDay());
            }
        }


        for (int i = 0; i < consList.size(); i++) {
            model.addConstr(consList.get(i), GRB.LESS_EQUAL, startTime.get(i), "c" + c_forName++);
        }
    }

    private void createConji() throws GRBException {
        List<GRBLinExpr> consList = new ArrayList<>();
        for (Spoj2 iSpoj : spoje2) {

            if (!iSpoj.getPossibleConnectionsToThis().isEmpty()) {
                GRBLinExpr cons = new GRBLinExpr();
                iSpoj.getPossibleConnectionsToThis().forEach(jSpoj
                        -> cons.addTerm(1, modelVariables.get(jSpoj.getId()).get(iSpoj.getId())));
                consList.add(cons);
            }
        }

        for (GRBLinExpr c: consList) {
            model.addConstr(c, GRB.LESS_EQUAL, 1, "c" + c_forName++);
        }

    }

    private void createConij() throws GRBException {
        List<GRBLinExpr> consList = new ArrayList<>();
        for (Spoj2 iSpoj : spoje2) {

            if (!iSpoj.getPossibleConnectionsFromThis().isEmpty()) {
                GRBLinExpr cons = new GRBLinExpr();
                iSpoj.getPossibleConnectionsFromThis().forEach(jSpoj
                        -> cons.addTerm(1, modelVariables.get(iSpoj.getId()).get(jSpoj.getId())));
                consList.add(cons);
            }
        }

        for (GRBLinExpr c: consList) {
            model.addConstr(c, GRB.LESS_EQUAL, 1, "c" + c_forName++);
        }

    }

    private void createObjFunction() throws GRBException {
        GRBLinExpr linExpr = new GRBLinExpr();

        GRBVar[] list = GurobiHelperFunctions.mapToArray(modelVariables);
        double[] coef = new double[list.length];
        Arrays.fill(coef, 1);
        linExpr.addTerms(coef, GurobiHelperFunctions.mapToArray(modelVariables));

        model.setObjective(linExpr, GRB.MAXIMIZE);
    }

    private void createVars() {

        spoje2.forEach(s -> {
            s.getPossibleConnectionsFromThis().forEach(n -> {
                try {
                    GRBVar grbVar = model.addVar(0, 1, 0, GRB.BINARY, "x" + s.getId() + "_" + n.getId());
                    modelVariables.computeIfAbsent(s.getId(), k -> new HashMap<>()).put(n.getId(), grbVar);
                } catch (GRBException e) {
                    e.printStackTrace();
                }
            });
        });

    }

//    public void solve(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje) {
//        randomTriangularTimeEnricher.enrich(spoje);
//
//        createModel(distances, spoje);
//
//        LpSolver lp = new LpSolver();
//        lp.solve(LP_MODEL);
//    }
//
//    public void createModel(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spoje) {
//        this.distances = distances;
//        this.spojeSimple = spoje;
//        this.spoje2 = NodesConnectionsCreator.createPossibleConnections(spojeSimple, distances);
//
//        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream(LP_MODEL), "utf-8"))) {
//
//            createLinearFunction(writer);
//            createSubject(writer);
//            createBinaries(writer);
//            writer.write("END");
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createSubject(Writer writer) throws IOException {
//        writer.write("Subject To\n");
//
//        int cNumber = 0;
//
//        for (int i = 0; i < spoje2.size(); i++) {
//            if (spoje2.get(i).getPossibleConnectionsFromThis().isEmpty())
//                continue;
//
//            writer.write(" c" + cNumber++ + ":\n");
//            String s = "  ";
//
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {
//
//                if (!s.equals(" ")) {
//                    s += "+";
//                }
//
//                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = "  ";
//                }
//            }
//            if (!s.equals(" ")) {
//                writer.write(s);
//                writer.write("\n");
//            }
//            writer.write("  <= 1\n");
//        }
//
//        for (int i = 0; i < spoje2.size(); i++) {
//            if (spoje2.get(i).getPossibleConnectionsToThis().isEmpty())
//                continue;
//
//            writer.write(" c" + cNumber++ + ":\n");
//            String s = "  ";
//
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsToThis().size(); j++) {
//
//                if (!s.equals(" ")) {
//                    s += "+";
//                }
//
//                s += "x" + spoje2.get(i).getPossibleConnectionsToThis().get(j).getId() + "_" + spoje2.get(i).getId();
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = "  ";
//                }
//            }
//            if (!s.equals(" ")) {
//                writer.write(s);
//                writer.write("\n");
//            }
//            writer.write("  <= 1\n");
//        }
//
//        for (int i = 0; i < spoje2.size(); i++) {
//            if (spoje2.get(i).getPossibleConnectionsToThis().isEmpty())
//                continue;
//
//            writer.write(" c" + cNumber++ + ":\n");
//            String s = "  ";
//
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsToThis().size(); j++) {
//
//                if (!s.equals(" ")) {
//                    s += "+";
//                }
//
//                //
//
//                //
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = "  ";
//                }
//            }
//            if (!s.equals(" ")) {
//                writer.write(s);
//                writer.write("\n");
//            }
//            writer.write("  <= 1\n");
//        }
//
//        for (int i = 0; i < spoje2.size(); i++) {
//            if (spoje2.get(i).getPossibleConnectionsFromThis().isEmpty())
//                continue;
//
//
//            writer.write(" c" + cNumber++ + ":\n");
//            String s = "  ";
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {
//
//                Spoj2 spojTo = spoje2.get(i).getPossibleConnectionsFromThis().get(j);
//                int dist = distances.get(spoje2.get(i).getToId()).get(spojTo.getFromId());
//
//                dist += GlobalConfig.M;
//
////
//                long a = (spoje2.get(i).getDeparture().toSecondOfDay() + spoje2.get(i).getTriangularTimeDurationSec().getRight() + dist);
//                long b = spojTo.getDeparture().toSecondOfDay();
//
//                Spoj2 tempFrom = spoje2.get(i);
//
//                if (a > b) {
//                    int aswqe = 1;
//                } else {
//                    continue;
//                    //
//                }
//
//
//                s += "" + (spoje2.get(i).getDeparture().toSecondOfDay() + spoje2.get(i).getTriangularTimeDurationSec().getRight() + dist);
//                s += "x" + spoje2.get(i).getId() + "_" + spojTo.getId();
//                s += "<" + spojTo.getDeparture().toSecondOfDay();
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = "  ";
//                }
//
//            }
//            if (!s.equals(" ")) {
//                writer.write(s);
//                writer.write("\n");
//            }
//
//        }
//
//        writer.write("\n");
//    }
//
//    private void createBinaries(Writer writer) throws IOException {
//        writer.write("int\n");
//        String s = " ";
//        for (int i = 0; i < spoje2.size(); i++) {
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {
//
//                if (!s.equals(" ")) {
//                    s += " ";
//                }
//
//                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = " ";
//                }
//            }
//        }
//        if (!s.equals(" ")) {
//            writer.write(s);
//            writer.write("\n\n");
//        }
//    }
//
//    private void createLinearFunction(Writer writer) throws IOException {
//        writer.write("Maximize\n");
//        writer.write(" obj:\n");
//
//        String s = " ";
//        for (int i = 0; i < spoje2.size(); i++) {
//            for (int j = 0; j < spoje2.get(i).getPossibleConnectionsFromThis().size(); j++) {
//
//                if (!s.equals(" ")) {
//                    s += "+";
//                }
//
//                s += "x" + spoje2.get(i).getId() + "_" + spoje2.get(i).getPossibleConnectionsFromThis().get(j).getId();
//
//                if (s.length() > MAX_C) {
//                    writer.write(s);
//                    writer.write("\n");
//                    s = " ";
//                }
//            }
//        }
//        if (!s.equals(" ")) {
//            writer.write(s);
//            writer.write("\n\n");
//        }
//    }
}
