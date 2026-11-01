# Student Assignment Tracker

A desktop application for students to track their subjects and assignments.
The user interface is built with Java Swing and the data is stored in SQLite.
It is plain Java: no Maven, no Gradle and no libraries except the SQLite JDBC jar.

## Requirements

- JDK 17 or newer
- The SQLite JDBC driver in `lib/sqlite-jdbc-3.53.4.0.jar`

## Project structure

```
andy_proj/
|-- src/
|   |-- Main.java
|   |-- gui/
|   |   |-- Theme.java
|   |   |-- MainWindow.java
|   |   |-- LoginPanel.java
|   |   |-- RegisterPanel.java
|   |   |-- ForgotPasswordPanel.java
|   |   |-- DashboardPanel.java
|   |   |-- SubjectPanel.java
|   |   `-- AssignmentDialog.java
|   |-- model/
|   |   |-- Person.java
|   |   |-- Student.java
|   |   |-- Subject.java
|   |   |-- Assignment.java
|   |   |-- Displayable.java
|   |   `-- SubjectProgress.java
|   |-- exception/
|   |   |-- InvalidLoginException.java
|   |   |-- DuplicateSubjectException.java
|   |   `-- InvalidDateException.java
|   |-- dao/
|   |   |-- DBConnection.java
|   |   |-- StudentDAO.java
|   |   |-- SubjectDAO.java
|   |   `-- AssignmentDAO.java
|   `-- util/
|       `-- DateHelper.java
|-- lib/
|   `-- sqlite-jdbc-3.53.4.0.jar
|-- schema.sql
|-- student_tracker.db
`-- README.md
```

27 files in total: 23 Java source files, the README, `schema.sql`,
the database file and the SQLite jar. Compiled `.class` files are not counted.

## Compile and run

Run the commands from the project folder. The database and `schema.sql` are
found using relative paths, so the working folder matters.

### Windows

```bat
javac -cp "lib/*" -d out src\Main.java src\model\*.java src\exception\*.java src\dao\*.java src\util\*.java src\gui\*.java
java -cp "out;lib/*" Main
```

### Linux or macOS

```bash
javac -cp "lib/*" -d out src/Main.java src/model/*.java src/exception/*.java src/dao/*.java src/util/*.java src/gui/*.java
java -cp "out:lib/*" Main
```

`lib/*` puts the SQLite jar on the classpath. The `out` folder only holds
compiled `.class` files and can be deleted at any time.

## First start

- On the first run, `DBConnection` creates `student_tracker.db` from `schema.sql`.
- To start with an empty database, close the program, delete
  `student_tracker.db`, and run the program again.
- The database is closed when the main window is closed.

## Using the program

### Account screens

1. Register with a name, email, password, security question and answer.
2. Log in with the email and password.
3. Reset a forgotten password with the security question.

Errors appear in a red label on the screen. Wrong login details show the
message from `InvalidLoginException`.

### Dashboard

- The three count cards show Pending, In Progress and Completed totals.
- The assignment table opens with the student's incomplete work, nearest due
  date first. The ID column is hidden.
- "Days Left" shows `3 days left`, `1 day left`, `Due today` or
  `Overdue by 2 days`. Overdue text is shown in muted red.
- The filter combo switches between Incomplete, Pending, In Progress and
  Completed.
- Search looks for a word in the title or in the subject name. Clear returns
  to the current filter.
- "+ Add Assignment" opens a dialog for the title, due date (`yyyy-MM-dd`)
  and subject. The subject combo lists existing subjects and also accepts a
  typed new name, which is saved automatically. New assignments are Pending.
- Edit changes the title and due date. Update Status uses a small option
  dialog. Delete asks for confirmation. These three actions need a selected
  row first, otherwise the program says "Please select an assignment first."

### Subjects

- The right-hand card shows each subject with its completion percentage from
  the `subject_progress` view and a progress bar.
- Clicking a subject name shows all of that subject's assignments, whatever
  their status. The table shows
  "Showing: <subject> (click here to clear)" above it.
- "Show all" returns to the default incomplete view.
- "+ Add Subject" asks for a name. A duplicate name shows the message from
  `DuplicateSubjectException`.
- "Remove Subject" needs a clicked subject, shows how many assignments will
  also be deleted, and asks for confirmation. The delete uses the existing
  transaction (commit/rollback) method.

## GUI files

| File | One-line description |
|---|---|
| `gui/Theme.java` | All colours, fonts and the reusable button, text-field and card styling helpers. |
| `gui/MainWindow.java` | The `JFrame` that uses `CardLayout` to switch between login, register, forgot password and dashboard. |
| `gui/LoginPanel.java` | Email and password form with Login, Create account and Forgot password links. |
| `gui/RegisterPanel.java` | Create-account form with name, email, password, security question and answer. |
| `gui/ForgotPasswordPanel.java` | Two-step password reset: find the account, show its question, then save a new password. |
| `gui/DashboardPanel.java` | Main screen: header, count cards, assignment toolbar, table, search/filter and refresh. |
| `gui/SubjectPanel.java` | Right column with subject progress bars, subject filtering, Show all, Add Subject and Remove Subject. |
| `gui/AssignmentDialog.java` | Modal `JDialog` shared by Add Assignment and Edit Assignment. |

## Swing concept to where it is used

| Concept | Where it is used | What it does |
|---|---|---|
| `JFrame` | `MainWindow` | The main application window. |
| `JPanel` | Every GUI class | Groups and arranges components. |
| `BorderLayout` | `MainWindow`, `DashboardPanel`, `SubjectPanel`, `AssignmentDialog` | Places parts in NORTH, CENTER, EAST, SOUTH and WEST. |
| `GridLayout` | Dashboard count cards, subject buttons | Gives components equal-sized cells. |
| `FlowLayout` | Header buttons, toolbar rows, dialog buttons | Lays components left to right. |
| `BoxLayout` | Account cards, dashboard toolbar, subject list | Stacks components vertically. |
| `CardLayout` | `MainWindow`, dashboard table area | Shows one "card" at a time, such as Login or Dashboard. |
| `JLabel` | Titles, field labels, messages, count numbers | Displays text. |
| `JButton` + `ActionListener` lambda | Every clickable button | A short lambda calls a named handler method. |
| `JTextField` | Email, name, search, title, due date | Single-line text input. |
| `JPasswordField` | Login, register, reset password | Hides the typed password. |
| `JComboBox` | Status filter, assignment subject | Chooses one item; the subject combo is editable. |
| `JTable` + `DefaultTableModel` | `DashboardPanel` | Shows assignments; `isCellEditable` returns false so cells are read-only. |
| `JScrollPane` | Assignment table, subject list | Adds scroll bars when content is too large. |
| `JProgressBar` | `SubjectPanel` | Shows subject progress from 0 to 100. |
| `JDialog` | `AssignmentDialog` | A modal window for adding or editing an assignment. |
| `JOptionPane` | Most screens | Shows confirmations, input boxes and friendly error messages. |
| `WindowAdapter` | `MainWindow` | Closes the database when the window closes. |
| `DefaultTableCellRenderer` | `DashboardPanel.DaysLeftRenderer` | Draws the "Overdue..." text in red. |

No layout manager other than BorderLayout, GridLayout, FlowLayout, BoxLayout
and CardLayout is used. There is no GridBagLayout, no third-party UI library,
no custom painting, no images, no threads and no SwingWorker.

## Viva questions about the GUI

**1. Where does the program start and why is `SwingUtilities.invokeLater` used?**
`Main.main` sets the look and feel, then calls `invokeLater` so the window is
created on Swing's Event Dispatch Thread. Swing components should be built and
changed on that thread.

**2. How does the application switch between screens?**
`MainWindow` holds a `JPanel` with `CardLayout`. Each screen is added as a
card. Methods such as `showLogin()` and `showDashboard(student)` call
`cardLayout.show(...)` to display the right card.

**3. How are button clicks handled?**
Each button gets a short lambda, for example
`loginButton.addActionListener(event -> handleLogin());`.
The lambda only calls a named method, so the real logic stays readable.

**4. How does the assignment table get its data and stay read-only?**
`DashboardPanel.refresh()` asks `AssignmentDAO` for a list, clears the
`DefaultTableModel` and adds one row per assignment. The model overrides
`isCellEditable` to return `false`, so users cannot type in cells.

**5. Why is there no SQL in the `gui` package?**
The DAO classes own all database work. The GUI creates DAO objects and calls
methods such as `getIncompleteAssignments` or `updateStatus`. This keeps the
screens separate from the SQL and makes both easier to explain and test.

**6. How does clicking a subject filter the table?**
`SubjectPanel` calls `DashboardPanel.showSubjectFilter(id, name)`, which uses
`AssignmentDAO.getAssignmentsBySubject`. That method returns every status, not
just incomplete ones. A small label shows the active subject, and "Show all"
calls `showAllAssignments()` to return to the Incomplete view.

**7. How is one dialog used for both Add and Edit?**
`AssignmentDialog` receives an `Assignment` or `null`. `null` means add mode:
the fields are empty and the subject combo is editable. A real assignment means
edit mode: title and date are pre-filled and the subject combo is disabled.

**8. How are due dates checked and overdue work shown?**
`DateHelper` parses the text with `LocalDate`, rejects text that is not a date
or is in the past, and turns the date into "3 days left", "Due today" or
"Overdue by 2 days". `DaysLeftRenderer` checks whether that text starts with
"Overdue" and paints it with the red theme colour.

## Database notes

- `Student`, `Subject` and `Assignment` are the three tables.
- `subject_progress` is a view that counts total and completed assignments.
- `idx_assignment_due_date` is an index on `Assignment(due_date)`.
- Foreign keys are switched on, and removing a subject deletes its assignments
  in one transaction with commit and rollback.
- Status values are stored as text: `Pending`, `In Progress`, `Completed`.
- The GUI only uses these DAO methods; it never writes SQL itself.

## Files created at runtime

- `student_tracker.db` - created on the first run if it is missing.

## Problems and fixes

- **SQLite driver not found:** make sure
  `lib/sqlite-jdbc-3.53.4.0.jar` exists and that the classpath contains
  `lib/*`.
- **Cannot read `schema.sql`:** start the program from the project folder.
- **Compiled files are missing:** run the `javac` command again; the `out`
  folder is only for `.class` files.
- **Colours look different on another computer:** `Main` selects the
  cross-platform look and feel so the theme colours are respected everywhere.
