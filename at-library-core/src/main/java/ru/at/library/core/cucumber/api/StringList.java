//package ru.at.library.core.cucumber.api;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StringList {
//    private final List<String> stringList = new ArrayList<>();
//
//    public void add(String item) {
//        stringList.add(item);
//    }
//
//    public void add(List<String> items) {
//        stringList.addAll(items);
//    }
//
//    public List<String> getList() {
//        return new ArrayList<>(this.stringList);
//    }
//
//    public List<String> takeList() {
//        List<String> errors = getList();
//        stringList.clear();
//        return errors;
//    }
//
//    public boolean contains(String item) {
//        return stringList.contains(item);
//    }
//
//    public boolean checkContainsAndRemove(String item) {
//        boolean contains = false;
//        if (stringList.contains(item)) {
//            stringList.remove(item);
//            contains = true;
//        }
//        return contains;
//    }
//
//    public boolean isEmpty() {
//        return stringList.isEmpty();
//    }
//
//    public int getCount() {return stringList.size(); }
//
//    public void clear() {
//        stringList.clear();
//    }
//}
