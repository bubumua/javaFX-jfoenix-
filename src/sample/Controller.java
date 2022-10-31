package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * 用于处理用户的交互
 *
 * @author Bubu
 */
public class Controller implements Initializable {
    
    // 视图控件
    @FXML
    private ComboBox<String> featureSelection;
    public ComboBox<String> distanceTypeSelection;
    public Button compare;
    public Label result;
    public TextArea textA;
    public TextArea textB;
    public Button btn_chooseTextA;
    public Button btn_clearA;
    public Button btn_chooseTextB;
    public Button btn_clearB;
    public ComboBox<String> presetFileSelectionA;
    public ComboBox<String> presetFileSelectionB;
    
    // 数据
    public static final String[] presetFiles = {"Person_A", "Person_B", "Person_C"};
    public static final String[] FEATURES = {"词频", "首次出现位置"};
    public static final String[] DISTANCE_TYPE = {"余弦相似度", "欧式距离", "汉明相似度"};
    Model model;
    
    
    /**
     * 初始化数据与控件（包括添加监听器等）
     *
     * @param location  URL
     * @param resources ResourceBundle
     * @Return void
     * @author Bubu
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize start");
        // 初始化模型
        model = new Model();
        // 初始化视图控件部分
        compare.setText("比较");
        textA.setText("");
        textB.setText("");
        // 初始化特征选择
        featureSelection.setItems(FXCollections.observableArrayList(FEATURES));
        featureSelection.setValue(FEATURES[0]);
        // 初始化特征向量距离选择
        distanceTypeSelection.setItems(FXCollections.observableArrayList(DISTANCE_TYPE));
        distanceTypeSelection.setValue(DISTANCE_TYPE[0]);
        
        result.setText("以" + featureSelection.getValue() + "为特征的" + distanceTypeSelection.getValue());
        
        // 监听特征选择
        featureSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                result.setText(FEATURES[(int) newValue]);
                result.setText("以" + featureSelection.getValue() + "为特征的" + distanceTypeSelection.getValue());
            }
        });
        
        // 监听特征向量距离选择
        distanceTypeSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                result.setText(DISTANCE_TYPE[newValue.intValue()]);
                result.setText("以" + featureSelection.getValue() + "为特征的" + distanceTypeSelection.getValue());
            }
        });
        
        // 预设文本选择
        presetFileSelectionA.setPromptText("选择预设文本");
        presetFileSelectionA.setItems(FXCollections.observableArrayList(presetFiles));
        presetFileSelectionA.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                textA.setText("");
                InputStream inputStream = getClass().getResourceAsStream("res/presets/" + presetFiles[newValue.intValue()] + ".txt");
                Scanner sc = new Scanner(inputStream);
                while (sc.hasNextLine()) {
                    textA.appendText(sc.nextLine());
                    textA.appendText("\n");
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        presetFileSelectionB.setPromptText("选择预设文本");
        presetFileSelectionB.setItems(FXCollections.observableArrayList(presetFiles));
        presetFileSelectionB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                textB.setText("");
                InputStream inputStream = getClass().getResourceAsStream("res/presets/" + presetFiles[newValue.intValue()] + ".txt");
                Scanner sc = new Scanner(inputStream);
                while (sc.hasNextLine()) {
                    textB.appendText(sc.nextLine());
                    textB.appendText("\n");
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        System.out.println("initialize end");
    }
    
    /**
     * 直接响应比较按钮的点击，比较两个文本的相似度
     *
     * @Return void
     * @author Bubu
     */
    public void compare() {
        result.setText("comparing......");
        String strA = textA.getText(),
                strB = textB.getText();
        int featureIndex = featureSelection.getSelectionModel().getSelectedIndex();
        int distanceType = distanceTypeSelection.getSelectionModel().getSelectedIndex();
        double similarity = model.compareByType(strA, strB, featureIndex, distanceType);
        result.setText("以" + FEATURES[featureIndex] + "为特征，其" + DISTANCE_TYPE[distanceType] + "为" + (distanceType == 1 ? similarity : new DecimalFormat("0.00%").format(similarity)));
    }
    
    /**
     * 直接响应左侧文本栏的清空按钮，将左侧文本清空
     *
     * @param actionEvent 点击事件
     * @Return void
     * @author Bubu
     */
    public void clearTextA(ActionEvent actionEvent) {
        presetFileSelectionA.setValue("选择预设文本");
        textA.setText("");
    }
    
    /**
     * 直接响应右侧文本栏的清空按钮，将右侧文本清空
     *
     * @param actionEvent 点击事件
     * @Return void
     * @author Bubu
     */
    public void clearTextB(ActionEvent actionEvent) {
        presetFileSelectionB.setValue("选择预设文本");
        textB.setText("");
    }
    
    /**
     * 直接响应左侧文本栏的选择文件按钮，选择文件后，将文件内容输出到左侧文本栏中
     *
     * @param actionEvent 点击事件
     * @Return void
     * @author Bubu
     */
    public void chooseFileA(ActionEvent actionEvent) {
        System.out.println("click left button");
        showFileInA(openFile());
    }
    
    /**
     * 直接响应右侧文本栏的选择文件按钮，选择文件后，将文件内容输出到右侧文本栏中
     *
     * @param actionEvent 点击事件
     * @Return void
     * @author Bubu
     */
    public void chooseFileB(ActionEvent actionEvent) {
        System.out.println("click left button");
        showFileInB(openFile());
    }
    
    /**
     * 新建窗口，选择文件
     *
     * @Return java.io.File 文件对象
     * @author Bubu
     */
    File openFile() {
        Stage stage = new Stage();
        File file = new FileChooser().showOpenDialog(stage);
        System.out.println("get file " + file.getName());
        return file;
    }
    
    public void showFileInA(File file) {
        showOnTextArea(file, textA);
    }
    
    public void showFileInB(File file) {
        showOnTextArea(file, textB);
    }
    
    /**
     * 给定文件，将内容输出到给定的文本栏中
     *
     * @param file     需要输出的文件
     * @param textArea 显示文件内容的文本栏
     * @Return void
     * @author Bubu
     */
    private void showOnTextArea(File file, TextArea textArea) {
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            
            textArea.setText("");
            while (scanner.hasNextLine()) {
                textArea.appendText(scanner.nextLine());
                textArea.appendText("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("file error");
            e.printStackTrace();
        }
    }
    
}