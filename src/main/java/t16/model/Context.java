package t16.model;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class Context {
    private Query.CONTEXT type;
    private String context;

    public Context(Query.CONTEXT type, String context) {
        this.type = type;
        this.context = context;
    }

    public Query.CONTEXT getType() {
        return type;
    }

    public String getContext() {
        return context;
    }

    @Override
    public String toString() {
        return context;
    }
}
