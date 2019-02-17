###### 二叉树删除节点

> 二叉树删除节点相对复杂一点

1. 被删除的节点是叶子节点
2. 被删除的节点只有左子节点
3. 被删除的节点只有右子节点
4. 被删除的有两个子节点

> 被删除的节点是叶子节点

1. 被删除的节点是root节点
rootNode = null
2. 被删除的节点是parent的左子节点
parent.leftChild = null
3. 被删除的节点是parent的右子节点
parent.rightChild = null

> 被删除的节点只有左子节点

1. 被删除的节点是root节点
rootNode = currNode.leftChild
2. 被删除的节点是parent的左子节点
parent.leftChild = currNode.leftChild
3. 被删除的节点是parent的右子节点
parent.rightChild = currNode.leftChild

> 被删除的节点只有右子节点

1. 被删除的节点是root节点
rootNode = currNode.rightChild
2. 被删除的节点是parent的左子节点
parent.leftChild = currNode.rightChild
3. 被删除的节点是parent的右子节点
parent.rightChild = currNode.rightChild

> 被删除的有两个子节点

这种情况最复杂，因为要考虑到删除之后顺序不能乱。所以需要考虑，在树中找到一个合适的节点来替换这个节点，
用这种方法来保持整个树的稳定。所以又一个问题又来了了，该找哪个节点来替换它？
结论是，需要在树中找出所有比被删除节点的值大的所有数，并在这些数中找出一个最小的数来。听起来很拗，
从被删除的节点出发经过它的右节点，然后右节点最左边的叶子节点就是我们要找的，它有一个专业名词叫中序后继节点。

- [画一个树，便于思考](image_files/tree.png)

> 参考
- http://blog.csdn.net/nzh1234/article/details/31076401


