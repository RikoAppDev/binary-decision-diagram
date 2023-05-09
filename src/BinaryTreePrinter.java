import java.io.PrintStream;

public class BinaryTreePrinter {

    private final Node root;

    public BinaryTreePrinter(Node root) {
        this.root = root;
    }

    private String traversePreOrder(Node root, boolean printNodes) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (printNodes) {
            sb.append("(").append(root.getVariable()).append(")").append(" | ").append(root.getBooleanFunction()).append(" | ").append(root);
        } else {
            sb.append("(").append(root.getVariable()).append(")").append(" | ").append(root.getBooleanFunction());
        }

        String pointerRight = "└──";
        String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null, printNodes);
        traverseNodes(sb, "", pointerRight, root.getRight(), false, printNodes);

        return sb.toString();
    }

    private void traverseNodes(StringBuilder sb, String padding, String pointer, Node node, boolean hasRightSibling, boolean printNodes) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            if (printNodes) {
                sb.append("(").append(node.getVariable()).append(")").append(" | ").append(node.getBooleanFunction()).append(" | ").append(node);
            } else {
                sb.append("(").append(node.getVariable()).append(")").append(" | ").append(node.getBooleanFunction());
            }

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null, printNodes);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false, printNodes);
        }
    }

    public void print(PrintStream os, boolean printNodes) {
        os.print(traversePreOrder(root, printNodes));
        os.close();
    }
}
