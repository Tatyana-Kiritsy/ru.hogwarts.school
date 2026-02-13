-- liquibase formatted sql

-- changeset tanya_kiritsy:1
CREATE INDEX student_name_index ON student (name);

-- changeset tanya_kiritsy:2
CREATE INDEX faculty_namecolor_index ON faculty (name, color);
