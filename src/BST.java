import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author rnzhou
 * @version 2019.10.19
 * @param <T>
 * 
 * 
 */
public class BST<T extends Comparable<? super T>> 
        implements BinarySearchTree<T>, BSTIteratorInterface<T> {

    private BinaryNode<T> root;
    private BSTIterator<T> bstIterator;

    /**
     * 
     */
    public BST() {
        root = null;
    }

    /**
     * 
     */
    @Override
    public void insert(T x) {
        root = insert(x, root);

    }

    /**
     * 
     */
    @Override
    public void remove(T x) {
        root = remove(x, root);

    }

    /**
     * 
     */
    @Override
    public T findMin() {
        return elementAt(findMin(root));
    }

    /**
     * 
     */
    @Override
    public T findMax() {
        return elementAt(findMax(root));
    }

    /**
     * 
     */
    @Override
    public T search(T x) {
        return elementAt(find(x, root));
    }

    /**
     * 
     */
    @Override
    public void clear() {
        root = null;

    }

    /**
     * 
     */
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Internal method to get element value stored in a tree node, with safe
     * handling of null nodes.
     *
     * @param node the node.
     * @return the element field or null if node is null.
     */
    private T elementAt(BinaryNode<T> node) {
        return (node == null) ? null : node.getElement();
    }

    // ----------------------------------------------------------
    /**
     * Internal method to insert a value into a subtree.
     *
     * @param x    the item to insert.
     * @param node the node that roots the subtree.
     * @return the new root of the subtree.
     * @throws DuplicateItemException if x is already present.
     */
    private BinaryNode<T> insert(T x, BinaryNode<T> node) {
        if (node == null) {
            return new BinaryNode<T>(x);
        }
        else if (x.compareTo(node.getElement()) < 0) {
            node.setLeft(insert(x, node.getLeft()));
        }
        else if (x.compareTo(node.getElement()) >= 0) {
            node.setRight(insert(x, node.getRight()));
        }
        else {
            throw new DuplicateItemException(x.toString());
        }
        return node;
    }

    // ----------------------------------------------------------
    /**
     * Internal method to remove a specified item from a subtree.
     *
     * @param x    the item to remove.
     * @param node the node that roots the subtree.
     * @return the new root of the subtree.
     * @throws ItemNotFoundException if x is not found.
     */
    private BinaryNode<T> remove(T x, BinaryNode<T> node) {
        // This local variable will contain the new root of the subtree,
        // if the root needs to change.
        BinaryNode<T> result = node;

        // If there's no more subtree to examine
        if (node == null) {
            throw new ItemNotFoundException(x.toString());
        }

        // if value should be to the left of the root
        if (x.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(x, node.getLeft()));
        }
        // if value should be to the right of the root
        else if (x.compareTo(node.getElement()) > 0) {
            node.setRight(remove(x, node.getRight()));
        }
        // If value is on the current node
        else {
            // If there are two children
            if (node.getLeft() != null && node.getRight() != null) {

                BinaryNode<T> temp = findMax(node.getLeft());

                remove(temp.getElement());
                node.setElement(temp.getElement());

                result = node;

            }
            // If there is only one child on the left
            else if (node.getLeft() != null) {
                result = node.getLeft();
            }
            // If there is only one child on the right
            else {
                result = node.getRight();
            }
        }
        return result;
    }

    // ----------------------------------------------------------
    /**
     * Internal method to find the smallest item in a subtree.
     *
     * @param node the node that roots the tree.
     * @return node containing the smallest item.
     */
    private BinaryNode<T> findMin(BinaryNode<T> node) {
        if (node == null) {
            return node;
        }
        else if (node.getLeft() == null) {
            return node;
        }
        else {
            return findMin(node.getLeft());
        }
    }

    // ----------------------------------------------------------
    /**
     * Internal method to find the largest item in a subtree.
     *
     * @param node the node that roots the tree.
     * @return node containing the largest item.
     */
    private BinaryNode<T> findMax(BinaryNode<T> node) {
        if (node == null) {
            return node;
        }
        else if (node.getRight() == null) {
            return node;
        }
        else {
            return findMax(node.getRight());
        }
    }

    // ----------------------------------------------------------
    /**
     * Internal method to find an item in a subtree.
     *
     * @param x    is item to search for.
     * @param node the node that roots the tree.
     * @return node containing the matched item.
     */
    private BinaryNode<T> find(T x, BinaryNode<T> node) {
        if (node == null) {
            return null; // Not found
        }
        else if (x.compareTo(node.getElement()) < 0) {
            // Search in the left subtree
            return find(x, node.getLeft());
        }
        else if (x.compareTo(node.getElement()) > 0) {
            // Search in the right subtree
            return find(x, node.getRight());
        }
        else {
            return node; // Match
        }
    }

    /**
     * Gets an in-order string representation of 
     *    the tree If the tree holds 5 / \ 2
     * 6 \ 3 It would print 2, 3, 5, 6
     * 
     * @return an in-order string representation of the tree
     */
    @Override
    public String toString() {
        if (root == null) {
            return "";
        }
        else {
            return root.toString();
        }
    }

    /**
     * 
     */
    @Override
    public Iterator<T> iterator() {
        bstIterator = new BSTIterator<T>(root);
        return bstIterator;
    }

    /**
     * 
     * @return this.bstIterator.getSortedNodes() 
     */
    public ArrayList<T> studentInArray() {
        this.bstIterator = new BSTIterator<T>(root);
        return this.bstIterator.getSortedNodes();
    }

    /**
     * @author rnzho
     * @version 2019.10.19
     * @param <A>
     */
    public class BSTIterator<A> implements Iterator<T> {

        private ArrayList<T> nodesSorted;
        private int index;

        /**
         * @param root root of the tree
         */
        public BSTIterator(BinaryNode<T> root) {
            nodesSorted = new ArrayList<T>();
            index = -1;
            inOrderTraversal(root);

        }

        /**
         * @return return nodesSorted
         */
        public ArrayList<T> getSortedNodes() {
            return nodesSorted;
        }

        /**
         * @param rootB root of the tree
         * 
         */
        public void inOrderTraversal(BinaryNode<T> rootB) {

            if (rootB == null) {
                return;
            }
            this.inOrderTraversal(rootB.getLeft());
            nodesSorted.add(rootB.getElement());
            this.inOrderTraversal(rootB.getRight());
        }

        /**
         * 
         */
        @Override
        public boolean hasNext() {
            return this.index + 1 < this.nodesSorted.size();
        }

        /**
         * 
         */
        @Override
        public T next() {
            return this.nodesSorted.get(++this.index);
        }

    }
    /**
     * @return return root of the tree
     */
    public BinaryNode<T> getRoot() {
        return root;
    }

}
