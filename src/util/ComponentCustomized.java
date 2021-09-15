package util;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ComponentCustomized {

    public static ProgressIndicator getProgressControl(char form, boolean stateOfProcess) {
        if(form == 'c') {
            if (stateOfProcess) {
                return new ProgressIndicator(0);
            } else if (!stateOfProcess) {
                return new ProgressIndicator();
            } else {
                return null;
            }
        } else if (form == 'b') {
            if (stateOfProcess) {
                return new ProgressBar(0);
            } else if (!stateOfProcess) {
                return new ProgressBar();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
