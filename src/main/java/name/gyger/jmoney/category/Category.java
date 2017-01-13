package name.gyger.jmoney.category;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Category {

    public enum Type {
        NORMAL, SPLIT, TRANSFER, ACCOUNT, ROOT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @ManyToOne
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderBy("name")
    private List<Category> children;

    private String name;

    private Type type = Type.NORMAL;

    @Transient
    private long parentId;

    @Transient
    @JsonIgnore
    private int level;

    public Category() {
    }

    public Category(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameIndented() {
        // TODO correct indentation
        return "L" + getLevel() + ": " + getName();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}