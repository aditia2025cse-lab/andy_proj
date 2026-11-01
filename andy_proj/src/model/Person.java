package model;

// Stores the common details shared by every person.
// This class is abstract because the program never creates a plain Person.
public abstract class Person {

    private String name;
    private String email;

    // Creates a person with a name and email.
    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Returns the person's name.
    public String getName() {
        return name;
    }

    // Changes the person's name.
    public void setName(String name) {
        this.name = name;
    }

    // Returns the person's email.
    public String getEmail() {
        return email;
    }

    // Changes the person's email.
    public void setEmail(String email) {
        this.email = email;
    }
}
