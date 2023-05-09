import java.util.*;

public class GenerateFunctions {
    private final int maxVar;
    private final Map<Integer, List<String>> functionMap = new HashMap<>();

    public GenerateFunctions(int maxVar, int numberOfBooleanFunction) {
        this.maxVar = maxVar;
        generateFunctions(numberOfBooleanFunction);
    }

    private void generateFunctions(int numberOfBooleanFunction) {
        Random random = new Random();

        for (int i = 2; i < maxVar + 1; i++) {
            Set<String> functionSet = new HashSet<>();

            while (functionSet.size() != numberOfBooleanFunction) {
                String booleanFunction = "";
                int functionsCount = random.nextInt(2, 16);

                for (int j = 0; j < functionsCount; j++) {
                    String function = "";
                    int functionLength = random.nextInt(1, i + 1);

                    for (int k = 65; k < 65 + functionLength; k++) {
                        int percent = random.nextInt(0, 100);
                        if (percent < 80) {
                            if (random.nextBoolean()) {
                                function = function.concat("!" + Character.toString(k));
                            } else {
                                function = function.concat(Character.toString(k));
                            }
                        }
                    }

                    if (function.length() == 0) {
                        if (random.nextBoolean()) {
                            function = "!" + Character.toString(random.nextInt(65, 65 + functionLength + 1));
                        } else {
                            function = Character.toString(random.nextInt(65, 65 + functionLength + 1));
                        }
                    }

                    booleanFunction = booleanFunction.concat(function + "+");
                }

                booleanFunction = booleanFunction.substring(0, booleanFunction.length() - 1);

                functionSet.add(booleanFunction);
            }
            functionMap.put(i, functionSet.stream().toList());
        }
    }

    public List<String> getFunctionsForVariable(int key) {
        return functionMap.get(key);
    }

    public Map<Integer, List<String>> getFunctionMap() {
        return functionMap;
    }

    public void printFunctions() {
        for (int i = 2; i < maxVar + 1; i++) {
            List<String> functionsList = getFunctionMap().get(i);

            System.out.println("Variables: " + i);
            for (String booleanFunction : functionsList) {
                System.out.println("\t" + booleanFunction);
            }
        }
    }
}
