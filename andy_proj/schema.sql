-- DDL: create the Student table with its constraints.
CREATE TABLE Student (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    security_question TEXT NOT NULL,
    security_answer TEXT NOT NULL
);

-- DDL + FOREIGN KEY + UNIQUE + NOT NULL: subjects belong to one student and
-- the same student cannot add the same subject name twice.
CREATE TABLE Subject (
    subject_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    subject_name TEXT NOT NULL,
    UNIQUE (student_id, subject_name),
    FOREIGN KEY (student_id) REFERENCES Student(student_id)
);

-- DDL + FOREIGN KEY + DEFAULT + CHECK: assignments belong to a student and a
-- subject, and the status must be one of the three allowed values.
CREATE TABLE Assignment (
    assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    due_date TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'Pending'
        CHECK (status IN ('Pending', 'In Progress', 'Completed')),
    FOREIGN KEY (student_id) REFERENCES Student(student_id),
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);

-- VIEW + JOIN + GROUP BY + COUNT: show total and completed assignments for
-- each subject in one reusable query.
CREATE VIEW subject_progress AS
SELECT s.subject_id AS subject_id,
       s.subject_name AS subject_name,
       COUNT(a.assignment_id) AS total_assignments,
       COUNT(CASE WHEN a.status = 'Completed' THEN 1 END) AS completed_assignments
FROM Subject s
LEFT JOIN Assignment a ON s.subject_id = a.subject_id
GROUP BY s.subject_id, s.subject_name;

-- INDEX: makes sorting and searching by due date faster.
CREATE INDEX idx_assignment_due_date ON Assignment(due_date);
