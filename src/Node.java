import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Node> parents = new ArrayList<>();
    private Node left, right;
    private final String booleanFunction;
    private final String variable;

    public Node(String booleanFunction, String variable) {
        this.booleanFunction = booleanFunction;
        this.variable = variable;
    }

    public List<Node> getParents() {
        return parents;
    }

    public void addParent(Node parent) {
        parents.add(parent);
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getBooleanFunction() {
        return booleanFunction;
    }

    public String getVariable() {
        return variable;
    }


}
