package ru.bcs.at.library.core.steps;

import cucumber.api.java.ru.И;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;
import static ru.bcs.at.library.core.core.helpers.PropertyLoader.loadValueFromFileOrVariableOrDefault;

/**
 * <h1>Шаги по работе с BizTalk</h1>
 */
public class BizTalkSteps {
    /**
     * Перемещение файла в BizTalk</p>
     */
    @И("^перемещещие файла из \"([^\"]*)\" в BizTalk$")
    public static void moveFilesToBizTalk(String fileToMove) throws IOException {
        //TODO проверить и реализовать для работы с BizTalk

        fileToMove = loadValueFromFileOrPropertyOrVariableOrDefault(fileToMove);
        fileToMove = loadValueFromFileOrVariableOrDefault(fileToMove);

        String movedFile = loadValueFromFileOrPropertyOrVariableOrDefault("BizTalk");

        Path temp = Files.move(Paths.get(fileToMove), Paths.get(movedFile));

        Assert.assertNotNull("Ошибка перемещения файла: " + fileToMove, temp);
    }

    /**
     * Перемещение файла в BizTalk$</p>
     */
    @И("^перемещещие файла из BizTalk в \"([^\"]*)\"$")
    public static void moveFilesFromBizTalk(String movedFile) throws IOException {
        //TODO проверить и реализовать для работы с BizTalk$

        String fileToMove = loadValueFromFileOrPropertyOrVariableOrDefault("BizTalk");

        movedFile = loadValueFromFileOrPropertyOrVariableOrDefault(movedFile);
        movedFile = loadValueFromFileOrVariableOrDefault(movedFile);

        Path temp = Files.move(Paths.get(fileToMove), Paths.get(movedFile));

        Assert.assertNotNull("Ошибка перемещения файла: " + fileToMove, temp);
    }
}
