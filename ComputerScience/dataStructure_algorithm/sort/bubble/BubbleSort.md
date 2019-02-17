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
public class BubbleSortTest {
    public static void main(String[] args) {
        int array[] = {44, 33, 40, 22, 10, 82, 10, 9, 8, 10, 20};
        LogTrack.w(Arrays.toString(bubbleSort(array)));
    }

    private static int[] bubbleSort(int array[]) {
        int lastIndex = array.length - 1;
        int preLastIndex = array.length - 2;
        int tmp = 0;
        for (int i = 0; i <= lastIndex; i++) {
            for (int j = 0; j <= preLastIndex - i; j++) {
                if (array[j] > array[j + 1]) {
                    tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
        return array;
    }
}
```