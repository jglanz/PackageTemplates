package core.actions.custom;

import global.utils.file.FileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Arsen on 09.01.2017.
 */
public class CreateFileAction extends SimpleAction {

    private File file;
    private String content;

    public CreateFileAction(File file, String content) {
        this.file = file;
        this.content = content;
    }

    @Override
    public boolean run() {
        if (file.isDirectory()) {
            isDone = FileWriter.createDirectory(file);
        } else {
            isDone = FileWriter.writeStringToFile(content, file);
        }
        return isDone;
    }

    @Override
    public boolean undo() {
        if(file.isDirectory()){
           isDone = !FileWriter.removeDirectory(file);
        } else {
            isDone = FileWriter.removeFile(file);
        }
        return !isDone;
    }
}
