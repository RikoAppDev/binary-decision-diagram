import java.util.*;

public class BDD {
    private Node root;
    private final int varCount;
    private int nodeCount;
    private String order;
    private final String booleanFunction;
    private final Node trueNode = new Node("1", "LEAF");
    private final Node falseNode = new Node("0", "LEAF");

    public BDD(String booleanFunction, String order) {
        this.booleanFunction = booleanFunction.toUpperCase();
        this.order = order.toUpperCase();
        varCount = order.length();
        this.nodeCount = 0;
    }

    private BDD BDD_create(String booleanFunction, String order) {
        List<Node> level = new ArrayList<>();
        root = new Node(booleanFunction, String.valueOf(order.charAt(0)));
        level.add(root);
        nodeCount++;

        for (int i = 0; i < order.length(); i++) {
            String s = String.valueOf(order.charAt(i));
            List<Node> nextLevel = new ArrayList<>();
            Map<String, Node> levelHashMap = new HashMap<>();

            for (Node actualNode : level) {
                Set<String> functions = new HashSet<>(List.of(actualNode.getBooleanFunction().split("\\+")));
                Set<String> rightChild = new HashSet<>();
                Set<String> leftChild = new HashSet<>();

                for (String function : functions) {
                    if (function.contains("!" + s)) {
                        if (function.equals("!" + s)) {
                            leftChild.add("1");
                        } else {
                            leftChild.add(function.replace("!" + s, ""));
                        }
                    } else if (function.contains(s)) {
                        if (function.equals(s)) {
                            rightChild.add("1");
                        } else {
                            rightChild.add(function.replace(s, ""));
                        }
                    } else {
                        leftChild.add(function);
                        rightChild.add(function);
                    }
                }

                String left = "";
                String right = "";
                if (leftChild.contains("1")) {
                    left = "1";
                } else {
                    for (String string : leftChild) {
                        left = left.concat(string + "+");
                    }
                    if (left.length() != 0) {
                        left = left.substring(0, left.length() - 1);
                    } else {
                        left = "0";
                    }
                }
                if (rightChild.contains("1")) {
                    right = "1";
                } else {
                    for (String string : rightChild) {
                        right = right.concat(string + "+");
                    }
                    if (right.length() != 0) {
                        right = right.substring(0, right.length() - 1);
                    } else {
                        right = "0";
                    }
                }

                if (i < order.length() - 1) {
                    String variable = String.valueOf(order.charAt(i + 1));

                    if (left.equals("0")) {
                        actualNode.setLeft(falseNode);
                    } else if (left.equals("1")) {
                        actualNode.setLeft(trueNode);
                    } else {
                        Node leftNode;
                        if (levelHashMap.containsKey(left)) {
                            leftNode = levelHashMap.get(left);
                            actualNode.setLeft(leftNode);
                            leftNode.addParent(actualNode);
                        } else {
                            leftNode = new Node(left, variable);
                            nodeCount++;
                            actualNode.setLeft(leftNode);
                            leftNode.addParent(actualNode);
                            nextLevel.add(leftNode);
                            levelHashMap.put(left, leftNode);
                        }
                    }
                    if (right.equals("0")) {
                        actualNode.setRight(falseNode);
                    } else if (right.equals("1")) {
                        actualNode.setRight(trueNode);
                    } else {
                        Node rightNode;
                        if (levelHashMap.containsKey(right)) {
                            rightNode = levelHashMap.get(right);
                            actualNode.setRight(rightNode);
                            rightNode.addParent(actualNode);
                        } else {
                            rightNode = new Node(right, variable);
                            nodeCount++;
                            actualNode.setRight(rightNode);
                            rightNode.addParent(actualNode);
                            nextLevel.add(rightNode);
                            levelHashMap.put(right, rightNode);
                        }
                    }
                } else {
                    if (left.equals("0")) actualNode.setLeft(falseNode);
                    else actualNode.setLeft(trueNode);

                    if (right.equals("0")) actualNode.setRight(falseNode);
                    else actualNode.setRight(trueNode);
                }
            }

            if (i > 0) {
                for (Node actualNode : level) {
                    if (actualNode.getLeft() == actualNode.getRight()) {
                        for (Node parent : actualNode.getParents()) {
                            if (actualNode == parent.getLeft()) {
                                parent.setLeft(actualNode.getLeft());
                                actualNode.getLeft().addParent(parent);
                            } else {
                                parent.setRight(actualNode.getRight());
                                actualNode.getRight().addParent(parent);
                            }
                        }
                        nodeCount--;
                    }
                }
            }
            level = nextLevel;
        }

        if (root.getLeft() == root.getRight()) {
            root.getLeft().addParent(null);
            root = root.getLeft();
            nodeCount--;
        }

        if (root.getVariable().equals("LEAF")) {
            nodeCount++;
        } else {
            nodeCount += 2;
        }

        return this;
    }

    private BDD BDD_create_with_best_order(String booleanFunction) {
        String newOrder = this.order;
        BDD minimalBdd = BDD_create(booleanFunction, newOrder);

        do {
            newOrder = carousel(newOrder);

            BDD bdd = new BDD(booleanFunction, newOrder);
            bdd.BDD_create(booleanFunction, newOrder);
            if (bdd.getNodeCount() < minimalBdd.getNodeCount()) {
                minimalBdd = bdd;
                minimalBdd.order = newOrder;
            }
        } while (!newOrder.equals(order));

        return minimalBdd;
    }

    private String BDD_use(BDD bdd, String input) {
        if (input.length() == varCount) {
            Map<String, String> vectorMap = new HashMap<>();
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == '0' || input.charAt(i) == '1')
                    vectorMap.put(String.valueOf(order.charAt(i)), String.valueOf(input.charAt(i)));
                else return "-1";
            }

            Node result = bdd.getRoot();

            while (result.getLeft() != null && result.getRight() != null) {
                String value = vectorMap.get(result.getVariable());

                if (value.equals("0")) {
                    result = result.getLeft();
                } else if (value.equals("1")) {
                    result = result.getRight();
                } else {
                    return "-1";
                }
            }

            return result.getBooleanFunction();
        }

        return "-1";
    }

    private BDD createFullBDD(String booleanFunction, String order) {
        List<Node> level = new ArrayList<>();
        root = new Node(booleanFunction, String.valueOf(order.charAt(0)));
        level.add(root);
        nodeCount++;

        for (int i = 0; i < order.length(); i++) {
            String s = String.valueOf(order.charAt(i));
            List<Node> nextLevel = new ArrayList<>();

            for (Node node : level) {
                List<String> functions = List.of(node.getBooleanFunction().split("\\+"));
                List<String> rightChild = new ArrayList<>();
                List<String> leftChild = new ArrayList<>();

                for (String function : functions) {
                    if (function.contains("!" + s)) {
                        if (function.equals("!" + s)) {
                            leftChild.add("1");
                        } else {
                            leftChild.add(function.replace("!" + s, ""));
                        }
                    } else if (function.contains(s)) {
                        if (function.equals(s)) {
                            rightChild.add("1");
                        } else {
                            rightChild.add(function.replace(s, ""));
                        }
                    } else {
                        leftChild.add(function);
                        rightChild.add(function);
                    }
                }

                String left = "";
                String right = "";
                if (leftChild.contains("1")) {
                    left = "1";
                } else {
                    for (String string : leftChild) {
                        left = left.concat(string + "+");
                    }
                    if (left.length() != 0) {
                        left = left.substring(0, left.length() - 1);
                    } else {
                        left = "0";
                    }
                }
                if (rightChild.contains("1")) {
                    right = "1";
                } else {
                    for (String string : rightChild) {
                        right = right.concat(string + "+");
                    }
                    if (right.length() != 0) {
                        right = right.substring(0, right.length() - 1);
                    } else {
                        right = "0";
                    }
                }

                String nextVariable = (i < order.length() - 1) ? String.valueOf(order.charAt(i + 1)) : "LIST";

                Node leftNode = new Node(left, nextVariable);
                Node rightNode = new Node(right, nextVariable);
                nodeCount += 2;

                node.setLeft(leftNode);
                node.setRight(rightNode);

                nextLevel.add(leftNode);
                nextLevel.add(rightNode);
            }
            level = nextLevel;
        }

        return this;
    }

    private String carousel(String newOrder) {
        String firstChar = String.valueOf(newOrder.charAt(0));
        newOrder = newOrder.substring(1);

        return newOrder.concat(firstChar);
    }

    public String checkResult(String input) {
        Map<String, String> vectorMap = new HashMap<>();
        for (int i = 0; i < input.length(); i++) {
            vectorMap.put(String.valueOf(order.charAt(i)), String.valueOf(input.charAt(i)));
        }

        String result = booleanFunction;
        for (int i = 0; i < order.length(); i++) {
            String variable = String.valueOf(order.charAt(i));
            String value = vectorMap.get(String.valueOf(order.charAt(i)));

            result = result.replaceAll(variable, value);
        }

        result = result.replaceAll("!0", "1");
        result = result.replaceAll("!1", "0");

        Set<String> functions = new HashSet<>(List.of(result.split("\\+")));
        if (functions.contains("1")) {
            return "1";
        } else {
            for (String function : functions) {
                if (function.contains("0")) {
                    result = "0";
                } else {
                    return "1";
                }
            }
        }
        return result;
    }

    public void checkAllInputs() {
        int combinationCount = (int) Math.pow(2, varCount);
        List<String> binValues = new ArrayList<>();

        for (int i = 0; i < combinationCount; i++) {
            String binValue = Integer.toBinaryString(i);
            while (binValue.length() < varCount) {
                String zero = "0";
                binValue = zero.concat(binValue);
            }
            binValues.add(binValue);
        }

        boolean output = true;
        for (String value : binValues) {
            String result = useBinaryDecisionDiagram(value);
            String expectedResult = checkResult(value);
            if (!result.equals(expectedResult)) {
                System.out.println("❌ for value " + value + " result: " + result + ", expected result: " + expectedResult);
                output = false;
            }
        }

        if (output) {
            System.out.println("✅ all result are CORRECT!");
        }
    }

    public Node getRoot() {
        return root;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getVarCount() {
        return varCount;
    }

    public String getOrder() {
        return order;
    }

    public BDD createWithReduction() {
        return BDD_create(booleanFunction, order);
    }

    public BDD createWithoutReduction() {
        return createFullBDD(booleanFunction, order);
    }

    public BDD createWithBestOrder() {
        return BDD_create_with_best_order(booleanFunction);
    }

    public String useBinaryDecisionDiagram(String input) {
        return BDD_use(this, input);
    }
}
