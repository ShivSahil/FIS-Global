package com.horizonxe;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {
    private static BufferedWriter writer;
    private final List<String> projects = new ArrayList<>();
    private final List<String> modules = new ArrayList<>();
    @FXML
    public ComboBox<String> projectMenu;
    @FXML
    public ComboBox<String> moduleMenu;
    public AnchorPane mainView;
    private File executableFile;
    private File dataFile;
    private boolean isWindows;

    public void initialize() throws IOException {
        newFile("exec");
        writer = Files.newBufferedWriter(Paths.get(String.valueOf(executableFile)));
        writer.write("");
        writer.flush();
        initData();
    }

    private void newFile(String type) throws IOException {
        isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        String username = System.getProperty("user.name");
        String windowsPath = "C:\\Users\\" + username + "\\FisGlobal\\";
        String linuxPath = "/home/" + username + "/FisGlobal/";
        switch (type) {
            case "exec" -> {
                if (isWindows) {
                    executableFile = new File(windowsPath + "exec.bat");
                } else {
                    executableFile = new File(linuxPath + "exec.sh");
                }
                executableFile.getParentFile().mkdir();
                executableFile.createNewFile();
                executableFile.setExecutable(true);
            }
            case "data" -> {
                if (isWindows) {
                    dataFile = new File(windowsPath + "data.csv");
                } else {
                    dataFile = new File(linuxPath + "data.csv");
                }

                dataFile.getParentFile().mkdir();
                dataFile.createNewFile();
                dataFile.setExecutable(true);
            }
        }
    }

    @FXML
    public void writeData() {
        try {
            if (!projectMenu.getValue().isEmpty()) {
                writeData(writer, projectMenu.getValue(), moduleMenu.getValue());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(BufferedWriter writer, String project, String module) throws IOException {
	 if (project.equalsIgnoreCase("Angular")) {
		String executeCmds = "cd C:\\Automation\\Dev\\Automation\\XEAngularAutomation ";    // Add execute before all commands here
                writer.append(executeCmds).append(" && mvn verify -Dcucumber.filter.tags=\"@").append(module).append("\" -Dbrowser1=Chrome");
		} else if (project.equalsIgnoreCase("NonAngular")) {
		String executeCmds = "cd C:\\Automation\\Dev\\Automation\\XEAutomation ";    // Add execute before all commands here
                writer.append(executeCmds).append(" && mvn verify -Dcucumber.filter.tags=\"@").append(module).append("\" -Dbrowser1=Chrome");
		} 
    }

    private void initData() throws IOException {
        newFile("data");
        String row;

        BufferedReader csvReader = new BufferedReader(new FileReader(dataFile));
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (data.length > 1) {
                if (!data[0].isBlank())
                    projects.add(data[0]);
                if (!data[1].isBlank())
                    modules.add(data[1]);
            } else {
                projects.add(data[0]);
            }
        }
        csvReader.close();

        if (projects.isEmpty())
            projects.add("Empty");
        if (modules.isEmpty())
            modules.add("Empty");

        projectMenu.setItems(FXCollections.observableArrayList(projects));
        moduleMenu.setItems(FXCollections.observableArrayList(modules));
    }

    @FXML
    public void triggerCmd() throws IOException {
        writeData();
        Stage stage = (Stage) mainView.getScene().getWindow();
        writer.close();
        if (isWindows) {
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"" + executableFile.getAbsolutePath() + "\"");
            stage.close();
        } else {
            stage.close();
        /*
		    Runtime.getRuntime().exec(System.getenv("TERM") + " -e bash -c " + file.getAbsolutePath() + ";read");
		*/
            Runtime.getRuntime().exec("alacritty" + " -e bash -c " + executableFile.getAbsolutePath() + ";read");

        }
    }

    @FXML
    public void addData() throws IOException {
        Stage stage = (Stage) mainView.getScene().getWindow();
        if (isWindows) {
            Runtime.getRuntime().exec("cmd /c start excel /K \"" + dataFile.getAbsolutePath() + "\"");
            stage.close();
        } else {
            stage.close();
        /*
		    Runtime.getRuntime().exec(System.getenv("TERM") + " -e bash -c " + file.getAbsolutePath() + ";read");
        */
            Runtime.getRuntime().exec("gio " + " open " + dataFile.getAbsolutePath());
        }
    }
}

