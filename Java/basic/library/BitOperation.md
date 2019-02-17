### java 位运算  


二进制表示    
Integer.toBinaryString(0)  

取反运算(~)   
Integer.toBinaryString(~0) = 11111111111111111111111111111111  (32个1)  

按位异或 (^)   
相异为1，相同为0；  
     
左移运算(<<)
二进制位全部左移若干位(左边的二进制丢弃，右边补零)  
Integer.toBinaryString(1 << 1)  =  10  
Integer.toBinaryString(Integer.MAX_VALUE << 1) = 11111111111111111111111111111110  

右移运算(>>)
将一个数的各二进制位全部右移若干位，正数左补0，负数左补1，右边丢弃。操作数每右移一位，相当于该数除以2.  
Integer.toBinaryString(Integer.MIN_VALUE) = 10000000000000000000000000000000  
Integer.toBinaryString(Integer.MIN_VALUE >> 2) = 11100000000000000000000000000000  

无符号右移运算(>>>)  
各个位向右移指定的位数。右移后左边突出的位用零来填充。移出右边的位被丢弃  

补码  
正整数的补码，与其源码相同；  
负整数的补码，是其源码整体求反，再加1；  
例如 1的源码是 00000000000000000000000000000001  
例如 1的反码是 11111111111111111111111111111110  
例如 1的补码是 11111111111111111111111111111111  


java API  
十进制转成十六进制         Integer.toHexString(int i)     
十进制转成八进制         	  Integer.toOctalString(int i)     
十进制转成二进制         	  Integer.toBinaryString(int i)     
十六进制转成十进制          Integer.valueOf("FFFF",16).toString()     
八进制转成十进制         	   Integer.valueOf("376",8).toString()     
二进制转成十进制         	   Integer.valueOf("0101",2).toString()   

