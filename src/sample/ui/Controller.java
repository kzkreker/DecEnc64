package sample.ui;

import javafx.scene.control.*;
import java.nio.charset.Charset;
import sample.lib.Base64Coder;

public class Controller {

    public   Button     StartButton;
    public   Button     ClearButton;
    public   Button     InvertButton;
    public   ComboBox   CodeCombo;
    public   ComboBox   ActCombo;
    public   Label      StatusLabel;
    public   TextArea   InputTextArea;
    public   TextArea   OutputTextArea;

    private  String  InputString, OutputString;
    private int selectedAct,selectedCharsetItemNumber;
    private  Charset selectedCharset;

    private  String[] Charsets =  {"UTF-8", "KOI8-R", "US-ASCII", "WINDOWS-1251"};

    /**
     *Обработчик нажатия кнопки выполнения кодирования -
     *генерируем необходимую кодировку, передаём текст,
     * который необходимо декодировать/кодировать
     */

    public void onStartButtonPress()
    {
        //выбираем кодировку
        selectedCharsetItemNumber = CodeCombo.getSelectionModel().getSelectedIndex();
        selectedCharset = Charset.forName(Charsets[selectedCharsetItemNumber]);

        //выбор действия
        selectedAct = ActCombo.getSelectionModel().getSelectedIndex();

        InputString= InputTextArea.getText();
        OutputString ="";

        //обработка действия
        if (selectedAct==0) {
            try {
             OutputString =
                     Base64Coder.encodeString(InputString,selectedCharset);
             StatusLabel.setText("Статус: кодирование прошло удачно" );
            }
            catch (Exception e) {
                StatusLabel.setText("Статус: " + e.toString());
            }
        }
        else if (selectedAct==1) {

            try {
             OutputString =
                        Base64Coder.decodeString(InputString,selectedCharset);
             StatusLabel.setText("Статус: декодирование прошло удачно" );
            }
            catch (Exception e) {
              StatusLabel.setText("Статус: " + e.toString());
            }
        }
        OutputTextArea.setText(OutputString);
    }

    /**
     *Обработчик нажатия кнопки очистки полей ввода/вывода
     */

    public void onClearButtonPress()
    {
        OutputTextArea.clear();
        InputTextArea.clear();
    }

    /**
     *Обработчик нажатия кнопки инверсии полей ввода/вывода
     */

    public void onInvertButtonPress()
    {
       InputString = InputTextArea.getText();
       InputTextArea.setText(OutputTextArea.getText());
       OutputTextArea.setText(InputString);
    }
}
