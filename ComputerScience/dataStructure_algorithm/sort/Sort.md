### 冒泡排序  

```
两层 for 循环
for  i in 0.. array.size-1
    for j in 0.. array.size-2
```
整体的思想就是，  
1.i=0，不断比较array[j]和array[j+1]的大小，如果array[j]较大，则交换两者，一直到数组结束；j in i..array.size-1  
2.不断重复1操作，直至i到array.size-1  

```
private static int[] bubbleSort(int array[]) {
    if (array == null || array.length <= 1) {
        return array;
    }
    for (int i = 0; i < array.length - 1; i++) {
        for (int j = 0; j < array.length - i - 1; j++) {
            if (array[j + 1] < array[j]) {
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
            }
        }
    }
    return array;
}
```
### 简单选择排序  
```
private static int[] selectionSort(int[] array) {
    if (array == null || array.length <= 1) {
        return array;
    }
    for (int i = 0; i < array.length - 1; i++) {
        int max = i;
        for (int j = i + 1; j < array.length; j++) {
            if (array[max] > array[j]) {
                max = j;
            }
            if (max != i) {
                int temp = array[max];
                array[max] = array[i];
                array[i] = temp;
            }
        }
    }
    return array;
}
```
![常见排序算法时间复杂度和空间复杂度](../ImageFiles/sort_001.png)  

http://www.jianshu.com/p/ae97c3ceea8d  
