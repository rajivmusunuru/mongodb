package dev.rajivm.model;

import org.bson.Document;

public class User {
    private String id;
    private String name;
    private String email;

    public User() {}

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Document toDocument() {
        Document doc = new Document();
        if (id != null) doc.append("_id", id);
        doc.append("name", name);
        doc.append("email", email);
        return doc;
    }

    public static User fromDocument(Document doc) {
        if (doc == null) return null;
        User u = new User();
        Object id = doc.get("_id");
        if (id != null) u.setId(id.toString());
        u.setName(doc.getString("name"));
        u.setEmail(doc.getString("email"));
        return u;
    }
}

