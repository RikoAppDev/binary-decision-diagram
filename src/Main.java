import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        class AvgData {
            final String order;
            final double avgReductionBy;
            final long avgCreationTime;

            public AvgData(String order, double avgReductionBy, long avgCreationTime) {
                this.order = order;
                this.avgReductionBy = avgReductionBy;
                this.avgCreationTime = avgCreationTime;
            }
        }
        /*
            AB+AC+BC
            A!C+B+!AB+!B
            A!C+ABC+!AB+!BC
            A!B!C+ABC+!AB!C+!A!BC
            ABCD+CDE+!B+CE+ACD+BDE
            A!B+!AB!C+!C+A!BCDE+C+AC!D+AB!C+A!B+A!C+BC+!ACD!E+!A!B!C+ABC!D+!ABCDE
            !A!CDE!FGHIJ+!A!B!DEFGH!IK+A!B!CDE!FG!HI!JL+ABC!DEFGH!IJ!KM+!A!B!C!DEFGHIJKL+A!BCD!EFGH!IJK!L!M+!ABC!D!EFGHIJ!K!L!M+!ABC!DE!F!GHIJKL+A!B!C!DEFGH!IJKL!M+!ABCD!E!F!GHIJK!L!M+A!BCDEFG!HIJK!L!M+!ABC!DEFGH!IJKLM+AB!CDEFGHIJKLM+!A!BCDEFGHI!JKLM+A!B!C!DE!FGHIJ!KLM
        */
        Scanner scanner = new Scanner(System.in);

        System.out.print("Automatic testing -> a | Manual testing -> m | EXIT -> e >> ");
        String testType = scanner.next();

        switch (testType) {
            case "a" -> {
                int maxVar;
                do {
                    System.out.print("Maximum variables (min 2, max 26) >> ");
                    maxVar = scanner.nextInt();
                } while (maxVar < 2 || maxVar > 26);

                System.out.print("Number of boolean functions for each order >> ");
                int numberOfBooleanFunction = scanner.nextInt();
                System.out.print("Do you want to check all BDDs (y / n) >> ");
                String checkBdd = scanner.next();

                GenerateFunctions generator = new GenerateFunctions(maxVar, numberOfBooleanFunction);
                List<AvgData> avgDataList = new ArrayList<>();

                String order = "A";
                for (int i = 66; i < 65 + maxVar; i++) {
                    order = order.concat(Character.toString(i));

                    long createWithReductionTime;
                    long createWithBestOrderTime;
                    long createTimeSum = 0;
                    double reductionSum = 0;
                    int counter = 0;

                    for (String function : generator.getFunctionsForVariable(i - 64)) {
                        BDD bdd = new BDD(function, order);

                        System.out.println("--------------------------------------------------------------------------------------");
                        System.out.println("-> Boolean function: " + function);
                        System.out.println("\t    Creation order: " + order);
                        System.out.println("\t Variables counter: " + bdd.getVarCount());
                        int fullBddNodesCount = (int) (Math.pow(2, order.length() + 1) - 1);
                        System.out.println("\t     Nodes counter: " + fullBddNodesCount);

                        long startCWR = System.nanoTime();
                        bdd = bdd.createWithReduction();
                        long endCWR = System.nanoTime();
                        createWithReductionTime = endCWR - startCWR;

                        int reducedBddRNodesCount = bdd.getNodeCount();
                        double reducedByR = 100 - (reducedBddRNodesCount * 100 / (double) fullBddNodesCount);
                        System.out.println("-> Created with reduction");
                        System.out.println("\t     Nodes counter: " + reducedBddRNodesCount);
                        System.out.println("\t        Reduced by: " + String.format("%.5f", reducedByR) + "%");
                        System.out.println("\t     Creation time: " + createWithReductionTime + "nanos");

                        if (checkBdd.equals("y")) {
                            bdd.checkAllInputs();
                        }

                        createTimeSum += createWithReductionTime;
                        reductionSum += reducedByR;
                        counter++;

                        long startCBO = System.nanoTime();
                        bdd = bdd.createWithBestOrder();
                        long endCBO = System.nanoTime();
                        createWithBestOrderTime = endCBO - startCBO;

                        int reducedBddBONodesCount = bdd.getNodeCount();
                        double reducedByBO = 100 - (reducedBddBONodesCount * 100 / (double) fullBddNodesCount);
                        System.out.println("-> Created with best order reduction");
                        System.out.println("\t     Best order is: " + bdd.getOrder());
                        System.out.println("\t     Nodes counter: " + bdd.getNodeCount());
                        System.out.println("\t        Reduced by: " + String.format("%.5f", 100 - (bdd.getNodeCount() * 100 / (double) reducedBddRNodesCount)) + "%");
                        System.out.println("\tTotal reduction by: " + String.format("%.5f", reducedByBO) + "%");
                        System.out.println("\t     Creation time: " + createWithBestOrderTime + "nanos");

                        if (checkBdd.equals("y")) {
                            bdd.checkAllInputs();
                        }

                        System.out.println("--------------------------------------------------------------------------------------");
                        System.out.println();
                    }
                    System.out.println("======================================================================================");
                    System.out.println("-> Variables count: " + order.length() + " | Order: " + order);
                    System.out.println("\t Average reduction by: " + String.format("%.5f", reductionSum / counter) + "%");
                    System.out.println("\tAverage creation time: " + createTimeSum / counter + "nanos");
                    avgDataList.add(new AvgData(order, reductionSum / counter, createTimeSum / counter));
                    System.out.println("======================================================================================");
                    System.out.println();
                }

                System.out.print("Do you wanna list all average data (y / n) >> ");
                String listData = scanner.next();
                if (listData.equals("y")) {
                    for (AvgData avgData : avgDataList) {
                        System.out.println("-> Variables count: " + avgData.order.length() + " | Order: " + avgData.order);
                        System.out.println("\t Average reduction by: " + String.format("%.5f", avgData.avgReductionBy) + "%");
                        System.out.println("\tAverage creation time: " + avgData.avgCreationTime + "nanos");
                        System.out.println();
                    }
                }

                try {
                    FileWriter avgDataWriter = new FileWriter("avg_data.csv");
                    for (AvgData avgData : avgDataList) {
                        avgDataWriter.write(avgData.order + ";" + avgData.order.length() + ";" + String.format("%.5f", avgData.avgReductionBy) + ";" + avgData.avgCreationTime + "\n");
                    }
                    avgDataWriter.close();
                    System.out.println("Successfully wrote to the file avg_data.csv");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
            case "m" -> {
                System.out.print("Boolean function >> ");
                String booleanFunction = scanner.next().toUpperCase();
                System.out.print("Order >> ");
                String order = scanner.next().toUpperCase();

                BDD bdd = new BDD(booleanFunction, order);
                System.out.print("Create with best order -> bo | Create with reduction -> r | Create without reduction -> wr | EXIT -> e >> ");
                String type = scanner.next();
                System.out.println("=============================================================================================================");
                System.out.println("\t         Boolean function: " + booleanFunction);
                switch (type) {
                    case "bo" -> {
                        bdd = bdd.createWithBestOrder();

                        BinaryTreePrinter printer = new BinaryTreePrinter(bdd.getRoot());
                        printer.print(new PrintStream("bdd_print.txt"), true);

                        int fullBddNodesCount = (int) (Math.pow(2, order.length() + 1) - 1);
                        int reducedBddNodesCount = bdd.getNodeCount();
                        System.out.println("\t            Best order is: " + bdd.getOrder());
                        System.out.println("\t        Variables counter: " + bdd.getVarCount());
                        System.out.println("\t   Full BDD nodes counter: " + fullBddNodesCount);
                        System.out.println("\tReduced BDD nodes counter: " + bdd.getNodeCount());
                        System.out.println("\t               Reduced by: " + String.format("%.5f", 100 - (reducedBddNodesCount * 100 / (double) fullBddNodesCount)) + "%");
                    }
                    case "r" -> {
                        bdd = bdd.createWithReduction();

                        BinaryTreePrinter printer = new BinaryTreePrinter(bdd.getRoot());
                        printer.print(new PrintStream("bdd_print.txt"), true);

                        int fullBddNodesCount = (int) (Math.pow(2, order.length() + 1) - 1);
                        int reducedBddNodesCount = bdd.getNodeCount();
                        System.out.println("\t           Creation order: " + order);
                        System.out.println("\t        Variables counter: " + bdd.getVarCount());
                        System.out.println("\t   Full BDD nodes counter: " + fullBddNodesCount);
                        System.out.println("\tReduced BDD nodes counter: " + bdd.getNodeCount());
                        System.out.println("\t               Reduced by: " + String.format("%.5f", 100 - (reducedBddNodesCount * 100 / (double) fullBddNodesCount)) + "%");
                    }
                    case "wr" -> {
                        bdd = bdd.createWithoutReduction();

                        BinaryTreePrinter printer = new BinaryTreePrinter(bdd.getRoot());
                        printer.print(new PrintStream("bdd_print.txt"), true);
                        System.out.println("\t           Creation order: " + order);
                        System.out.println("\t        Variables counter: " + bdd.getVarCount());
                        System.out.println("\t            Nodes counter: " + bdd.getNodeCount());
                    }
                    default -> {
                    }
                }

                System.out.println("=============================================================================================================");
                System.out.print("Automatic check -> a | Manual check -> m | EXIT -> e >> ");
                String checkType = scanner.next();
                switch (checkType) {
                    case "a" -> bdd.checkAllInputs();
                    case "m" -> {
                        System.out.print("Input vector value for use created BDD (EXIT = e) >> ");
                        String vector = scanner.next();

                        while (!vector.equals("e")) {
                            String result = bdd.useBinaryDecisionDiagram(vector);

                            if (result.equals("0") || result.equals("1")) {
                                String expectedResult = bdd.checkResult(vector);
                                if (result.equals(expectedResult)) {
                                    System.out.println("✅ result: " + result);
                                } else {
                                    System.out.println("❌ result: " + result + ", expected result: " + expectedResult);
                                }
                            } else {
                                System.out.println("⛔ Error, result: " + result);
                            }

                            System.out.print("Input vector value for use created bdd (EXIT = e) >> ");
                            vector = scanner.next();
                        }
                    }
                    default -> {
                    }
                }
                System.out.println("=============================================================================================================");
            }
            default -> {
            }
        }
    }
}