package ru.at.library.core.utils.selenide;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ElementChecker {

    public static List<IElementCheck> checkElements(List<IElementCheck> elementCheckList, long timeOutInMillis) {
        long time = System.currentTimeMillis() + timeOutInMillis;
        while (time > System.currentTimeMillis()) {
            elementCheckList.stream()
                    .filter(elementCheck -> !elementCheck.getStatus())
                    .forEach(elementCheck -> elementCheck.setStatus(elementCheck.getElement().is(elementCheck.getCondition())));
            if (elementCheckList.stream().allMatch(IElementCheck::getStatus)) break;
        }
        return elementCheckList;
    }

    public static String elementCheckListAsString(List<IElementCheck> elementCheckList) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (IElementCheck elementCheck:elementCheckList) {
            sb.append(elementCheck.toString());
            index++;
            if (index == elementCheckList.size()) break;
            sb.append("\r\n\r\n");
        }
        return sb.toString();
    }

    public static String elementFailedCheckListAsString(List<IElementCheck> elementCheckList) {
        List<IElementCheck> collectFailedElementCheck = elementCheckList.stream().filter(r -> !r.getStatus()).collect(toList());

        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (IElementCheck elementCheck:collectFailedElementCheck) {
            sb.append(elementCheck.toString());
            index++;
            if (index == collectFailedElementCheck.size()) break;
            sb.append("\r\n\r\n");
        }
        return sb.toString();
    }

}
