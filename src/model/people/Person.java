
package model.people;


public abstract class Person {
    protected String id;
    protected String name;
    protected String phone;

    public Person(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }
    
    public Person(Person p) {
        this.id = p.id;
        this.name = p.name;
        this.phone = p.phone;
    }
    
    public Person() {
        this.id = " ";
        this.name = " ";
        this.phone = " ";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
