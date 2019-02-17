### python 函数库  
操作系统接口  
import os  

日常的文件和目录管理任务  
import shutil  

文件通配符，glob 模块提供了一个函数用于从目录通配符搜索中生成文件列表  
 import glob  

 通用工具脚本经常调用命令行参数  
 import sys  

 字符串正则匹配  
 import re  
  
math 模块为浮点运算提供了对底层C函数库的访问:  
import math  

随机数的工具  
import random  

互联网访问  
```
import http.client
import json
import urllib.parse  
from urllib.request import urlopen  

```
日期和时间  
from datetime import date  

数据压缩  
import zlib  

性能度量  
from timeit import Timer  

### 日志  
logging 模块提供了完整和灵活的日志系统。它最简单的用法是记录信息并发送到一个文件或 sys.stderr:  
示例 1  
```
import logging
logging.debug('Debugging information')
logging.info('Informational message')
logging.warning('Warning:config file %s not found', 'server.conf')
logging.error('Error occurred')
logging.critical('Critical error -- shutting down')

```
示例 2  
```
import logging

logging.basicConfig(level=logging.DEBUG,
                    # filename='output.log',
                    datefmt='%Y/%m/%d %H:%M:%S',
                    format='%(asctime)s - %(name)s - %(levelname)s - %(lineno)d - %(module)s - %(message)s')
logger = logging.getLogger(__name__)

logger.info('This is a log info')
logger.debug('Debugging')
logger.warning('Warning exists')
logger.info('Finish')
```
参考  
https://cuiqingcai.com/6080.html  

十进制浮点数算法  
from decimal import *  

输出格式  
美化打印  
import pprint  
textwrap 模块格式化文本段落以适应设定的屏宽:  
import textwrap  
