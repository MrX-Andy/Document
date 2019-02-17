### 选择排序  

```
两层 for 循环
for  i in 0.. array.size-1
    for j in i.. array.size-1
```
基本算法思路  
i = 0，通过 for j 先找到数组中最大的元素的 下标 x ， 让 array[0] 和 array[x] 交换元素， i++；  
i = 1， 通过 for j 找到 剩下的 未排序的 素组中， 最大的元素的 下标 x ，让 array[1] 和 array[x] 交换元素， i++；  
...  
i = array.size -1  

```

public class SelectionTest {
    public static void main(String[] args) {
        int array[] = {44, 33, 40, 22, 10, 82, 10, 9, 8, 10, 20};
        LogTrack.w(Arrays.toString(selectionSort(array)));
    }

    private static int[] selectionSort(int[] array) {
        int lastIndex = array.length - 1;
        int k = 0;
        int tmp = 0;
        for (int i = 0; i <= lastIndex; i++) {
            k = i;
            for (int j = i; j <= lastIndex; j++) {
                if (array[k] > array[j]) {
                    k = j;
                }
            }
            tmp = array[k];
            array[k] = array[i];
            array[i] = tmp;
        }
        return array;
    }
}
```