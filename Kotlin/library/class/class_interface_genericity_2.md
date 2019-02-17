###### 泛型

> 泛型 2

```
public interface Comparable<in T> {
    public operator fun compareTo(other: T): Int
}
open class BinaryTreeNode<T : Comparable<T>>(
        var data: T,
        var left: BinaryTreeNode<T>? = null,
        var right: BinaryTreeNode<T>? = null
)
interface Tree<in T : Comparable<T>> {      }
interface BinTree<in T : Comparable<T>> : Tree<T> {     }
class BinaryTree<T : Comparable<T>> : BinTree<T> {
    private var rootNode: BinaryTreeNode<T>? = null
    /**先序遍历：根左右*/
    override fun preOrderAsList(): List<Comparable<T>>? {
        val dataList = ArrayList<T>()
        preOrder(dataList, rootNode)
        return dataList
    }

    //rootNode: BinaryTreeNode<T>
    private fun <T : Comparable<T>> preOrder(list: ArrayList<T>, node: BinaryTreeNode<T>?) {
        if (node != null) {
            list.add(node.data)
            preOrder(list, node.leftChild)
            preOrder(list, node.rightChild)
        }
    }
}
```