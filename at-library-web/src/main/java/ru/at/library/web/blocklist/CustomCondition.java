package ru.at.library.web.blocklist;

public class CustomCondition {
   public enum Comparison {
        equal("равно"),
        not_equal("не равно"),
        more("больше"),
        less("меньше"),
        more_or_equal("больше или равно"),
        less_or_equal("меньше или равно");

        public String name;

        Comparison(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static boolean comparisonInt(Comparison comparison, int actual, int comparedNumber) {
        boolean conditionSuccessful = false;
     
        switch (comparison) {
            case equal: {
                conditionSuccessful = actual == comparedNumber;
                break;
            }
            case not_equal:{
                conditionSuccessful = actual != comparedNumber;
                break;
            }
            case more:{
                conditionSuccessful = actual > comparedNumber;
                break;
            }
            case less:{
                conditionSuccessful = actual < comparedNumber;
                break;
            }
            case more_or_equal:{
                conditionSuccessful = actual >= comparedNumber;
                break;
            }
            case less_or_equal:{
                conditionSuccessful = actual <= comparedNumber;
                break;
            }
        }
        return conditionSuccessful;
    }
}
