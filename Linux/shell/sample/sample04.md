### 杀死java进程
```
#!/bin/bash
result=$(pgrep java)
if [ -z "$result" ]; then
    echo "没有java进程在运行"
fi
if [ -n "$result" ]; then
    echo 杀死 java进程，pid = $result
    kill -9 $result
fi

```