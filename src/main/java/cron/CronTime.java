package cron;

import exceptions.CronParseException;

import java.util.Arrays;
import java.util.List;

class CronTime {

    private final String expression;
    private final ExpressionType expressionType;
    private final TimeType timeType;

    public CronTime(String expression, TimeType timeType) {
        this.timeType = timeType;
        this.expression = expression;
        expressionType = validate();
    }

    public boolean evaluate(int value) {
        switch (expressionType) {
            case ANY:
                return true;
            case SINGLE:
                final int i = Integer.parseInt(expression);
                return i == value;
            case LIST:
                return Arrays.stream(expression.split(","))
                        .map(Integer::parseInt)
                        .anyMatch(listValue -> listValue == value);
            case RANGE:
                final String[] rangeArray = expression.split("-");
                final int lower = Integer.parseInt(rangeArray[0]);
                final int upper = Integer.parseInt(rangeArray[1]);
                return value >= lower && value <= upper;
            case STEP:
                final String[] stepArray = expression.split("/");
                final int step = Integer.parseInt(stepArray[1]);
                return value % step == 0;
        }
        return false;
    }

    private ExpressionType validate() {
        if (expression == null) {
            throw new CronParseException("Value must not be null");
        }

        // Any check
        if ("*".equals(expression)) {
            return ExpressionType.ANY;
        }

        final int lowerBound = timeType.getLowerBound();
        final int upperBound = timeType.getUpperBound();

        // Single check
        if (expression.length() == 1) {
            int i = Integer.parseInt(expression);
            if (numOutOfRange(i, lowerBound, upperBound)) {
                throw new CronParseException("Value is invalid: "+ i);
            }
            return ExpressionType.SINGLE;
        }

        // List Check
        if (expression.contains(",")) {
            final List<String> split = Arrays.stream(expression.split(",")).toList();
            for (String s : split) {
                int i = Integer.parseInt(s);
                if (numOutOfRange(i, lowerBound, upperBound)) {
                    throw new CronParseException("Value is in valid: " + i);
                }
            }
            return ExpressionType.LIST;
        }

        // Range Check
        if (expression.contains("-")) {
            final String[] split = expression.split("-");
            if (split.length != 2) {
                throw new CronParseException("Range must have the following format: x-y");
            }
            final int lower = Integer.parseInt(split[0]);
            final int upper = Integer.parseInt(split[1]);
            if (numOutOfRange(lower, lowerBound, upperBound)) {
                throw new CronParseException("Value is invalid: " + lower);
            }
            if (numOutOfRange(upper, lowerBound, upperBound)) {
                throw new CronParseException("Value is invalid: " + upper);
            }
            if (upper - lower <= 0) {
                throw new CronParseException("The upper value of a range must be greater than the lower value");
            }
            return ExpressionType.RANGE;
        }

        // Step Check
        if (expression.contains("/")) {
            final String[] split = expression.split("/");
            if (!"*".equals(split[0])) {
                throw new CronParseException("Step expression must have the following format: */x where x is an integer");
            }
            final int i = Integer.parseInt(split[1]);
            if (numOutOfRange(i, 1, upperBound)) {
                throw new CronParseException("Step value must be in range: 1-" + upperBound);
            }
            return ExpressionType.STEP;
        }

        throw new CronParseException(expression + " does not match a valid cron expression type");
    }

    private boolean numOutOfRange(int value, int lower, int upper) {
        return value < lower || value > upper;
    }

}
