package t16.model;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 16/03/2017
 */
public class Age {
    private Query.AGE type;
    private String age;

    public Age(Query.AGE type, String age) {
        this.type = type;
        this.age = age;
    }

    public Query.AGE getType() {
        return type;
    }

    public String getAge() {
        return age;
    }

    @Override
    public String toString() {
        return age;
    }
}
