### 文本字符串  

### 正则表达式  

♬ r  
是为了告诉编译器这个string是个raw string，不要转意backslash '\' 。 例如，\n 在raw string中，是两个字符，\和n， 而不会转意为换行符。  
由于正则表达式和 \ 会有冲突，因此，当一个字符串使用了正则表达式后，最好在前面加上'r'。  

字符串模板  
${string}  
print('hello ' + str(i) + '.mp4')  
print(f'hello {i}.mp4')
print(F'hello {i}.mp4')

参考  
http://www.cnblogs.com/c-x-a/p/9333826.html  


♬ findall  
 ```
 (pattern: Pattern[AnyStr], string: AnyStr, flags: Union[int, RegexFlag]) -> list  
 ```
 