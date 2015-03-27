package pt.ulisboa.tecnico.cmov.airdesk.core;

import java.io.File;

public class MyFile extends File {

    private boolean isBeingEdited;

    public MyFile(File dir, String name) {
        super(dir, name);
    }

}
