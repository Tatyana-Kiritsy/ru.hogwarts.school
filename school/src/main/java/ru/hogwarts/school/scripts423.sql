select s."name" , s.age, f."name"
from student s
join faculty f on s.id = f.id;

select s."name", s.age, a.file_path
from student s
inner join avatar a on s.id = a.student_id;