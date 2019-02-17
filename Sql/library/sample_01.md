查询 学分 大于等于 ‘id 等于s_2’ 的信息  
```
-- 包含 s_2 自身

select * from t_student where t_student.credit >= (
	select t_student.credit from t_student where t_student.sid = 's_2'
) ;

-- 不含 s_2 自身
select * from t_student where t_student.credit >= (
	select t_student.credit from t_student where t_student.sid = 's_2'
) and t_student.cid != 's_2';

```
查询 ’学分最低的人’ 的信息  
```
select * from t_student where t_student.credit = (
	select min(t_student.credit) from t_student
);
```
查询 ‘学分最低的人’ 中 ‘学号最高的’  的人的信息  
```
select * from t_student where t_student.sid = (
	select max(t_student.sid) from t_student where t_student.credit = (
		select min(t_student.credit) from t_student
	)
);
```
查询 ‘学分大于等于平均学分的’ 人的信息  
```
select * from t_student where t_student.credit >= (
	select avg(t_student.credit) from t_student
);
```
查询 ‘学分大于等于平均学分的’ 人 中 ‘学号最大的’ 人的姓名  
```
select t_student.sname from t_student where t_student.cid = (
	select max(t_student.cid) from t_student where (
		select avg(t_student.credit) from t_student
    )
);
```

正序输出最后5条数据  
```
select * from t_user where id > (select count(id) from t_user) - 5;  
```
