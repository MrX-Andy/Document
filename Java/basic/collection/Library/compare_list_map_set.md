### 集合框架的用法比较  
Tree 不允许 key为空；  
Concurrent 涉及 key，key不允许为空； 涉及value，value不允许为空；  
◆ HashMap
键值对，一组key、value元素；  
key、value 都可以为空（允许多个不同的key，value为空；）；  
key相同，value会被覆盖；  
迭代时，按照key的hashCode顺序；  

◆ Hashtable
键值对，一组key、value元素；  
key 和 value，均不可以为空（ hashtable.put(null,"AA");     hashtable.put("AA",null);  都会抛异常）；    

◆ TreeMap  
键值对，一组key、value元素；  
只允许value为空（treeMap.put(null, "AA"); 会抛异常）；  
迭代时，按照key的自然语言升序；  

◆ LinkedHashMap  
键值对，一组key、value元素；  
key、value 都可以为空（允许多个不同的key，value为空；）；  
key相同，value会被覆盖；  
迭代时，可以按照存入时的顺序，也可以按照LRU算法；  

◆ HashSet
不是键值对，只有一个元素；  
允许放入 null；  
放入重复元素，会被覆盖；  
迭代时，按照hashCode顺序；  

◆ LinkedHashSet  
不是键值对，只有一个元素；  
允许放入 null；  
放入重复元素，会被覆盖；  
迭代时，按照存入的顺序；  

◆ TreeSet  
不是键值对，只有一个元素；  
不能放入 null（treeSet.add(null); 会抛异常）；  
迭代时，按照自然语言升序；  

◆ ConcurrentHashMap
键值对，一组key、value元素；  
key 和 value，均不可以为空（ concurrentHashMap.put(null,"AA");     concurrentHashMap.put("AA",null);  都会抛异常）；  




