package org.apache.directory.ldap.client.api.search;

/**
 * @author Yuriy Tumakha
 */
public class ExpressionFilter implements Filter {
    
    private String expression;
    
    private ExpressionFilter(String expression) {
        this.expression = expression;
    }

    @Override
    public StringBuilder build() {
        return build(new StringBuilder());
    }

    @Override
    public StringBuilder build(StringBuilder builder) {
        if (!expression.startsWith("(")) {
            builder.append("(");
        }
        builder.append(expression);
        if (!expression.endsWith(")")) {
            builder.append(")");
        }
        return builder;
    }

    /**
     * Creates a new Expression FilterBuilder
     */
    public static FilterBuilder expression(String expression) {
        return new FilterBuilder(new ExpressionFilter(expression));
    }

}