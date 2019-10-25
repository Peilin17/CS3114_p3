
import java.util.Stack;

/**
 * 
 * @author peilin17
 * @version 2019.10.19
 * @param <T>
 * 
 * 
 */
public class BSTIterator<T> {
    /**
     * warnings
     */
    @SuppressWarnings("rawtypes")
    private Stack<BinaryNode> stack = new Stack<BinaryNode>();

    /**
     * put the data into stack
     * @param root tree root
     */
    public BSTIterator(BinaryNode<T> root) {

        while (root != null) {
            stack.push(root);
            root = root.getLeft();
        }
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return !stack.isEmpty();

    }

    /** @return the next smallest number */
    @SuppressWarnings("unchecked")
    public BinaryNode<T> next() {
        BinaryNode<T> minCurrent = stack.pop();
        if (minCurrent.getRight() != null) {
            BinaryNode<T> rightNode = minCurrent.getRight();
            while (rightNode != null) {
                stack.push(rightNode);
                rightNode = rightNode.getLeft();
            }
        }
        return minCurrent;
    }
}