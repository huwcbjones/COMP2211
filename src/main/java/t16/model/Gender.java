package t16.model;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class Gender {
    private Query.GENDER type;
    private String gender;

    public Gender(Query.GENDER type, String gender) {
        this.type = type;
        this.gender = gender;
    }

    public Query.GENDER getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return gender;
    }
}
