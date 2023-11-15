package trainer.resources;

import java.util.ListResourceBundle;

@SuppressWarnings("NewClassNamingConvention")
public class StringsResourceRB_en extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"topLabel", "Deluxe Ski Jump 2 Trainer by Mtik333"},
                {"detectButton", "Detect DOSBox with DSJ2"},
                {"windStrength", "Wind strength"},
                {"windDirection", "Wind direction (0 means right arrow)"},
                {"speedJumper", "Speed up jumper (default hotkey: F11)"},
                {"liftJumper", "Lift up jumper (default hotkey: F12)"},
                {"increaseWind", "Increase wind by (default hotkey: F6)"},
                {"decreaseWind", "Decrease wind (default hotkey: F7)"},
                {"activate", "Activate (default hotkey: F9)"},
                {"deactivate", "Deactivate (default hotkey: F10)"},
                {"help", "Help"},
                {"exit", "Exit"},
                {"message", "Message"},
                {"noDOSBox", "Couldn't find DOSBox process"},
                {"noDSJ2", "DOSBox launched, but couldn't find launched DSJ2\n" +
                        "Please check if you have NVIDIA Shadowplay launched,\n" +
                        "try to disable it and restart DOSBox and trainer.\n" +
                        "If this still does not help, please contact me."},
                {"dsj2Unloaded", "DSJ2 has been unloaded from DOSBox, stopping trainer"},
                {"foundDSJ2", "Found DSJ 2.1 process!"},
                {"foundDSJ2P", "Found DSJ 2.1P process!"},
                {"helpText", "Please read instruction on https://github.com/Mtik333/DSJ2Trainer\n\n" +
                        "1. Open DOSBox\n2. Launch DSJ2 in DOSBox\n3. Click on detect button\n" +
                        "4. Set values on sliders\n5. Click activation button\n6. Go ahead and jump"},
                {"saveValues", "Save values"},
                {"loadValues", "Load values"},
                {"activateHotkey", "Activate hotkey"},
                {"deactivateHotkey", "Deactivate hotkey"},
                {"speedUpHotkey", "Speed up hotkey"},
                {"liftUpHotkey", "Lift up hotkey"},
                {"increaseWindHotkey", "Increase wind hotkey"},
                {"decreaseWindHotkey", "Decrease wind hotkey"},
                {"changeHotkeys", "Change hotkeys"},
                {"savedValuesSuccess", "Values saved successfully"},
                {"loadedValuesSuccess", "Values loaded successfully"},
                {"thisIsDSJ2P", "Found version DSJ 2.1P, it's currently unsupported"},
                {"loadingValuesError", "Error during loading values from file, please check file format." +
                        "\nIt should like as follows: \n" +
                        "speedUp=0.0\n" +
                        "windStrength=3.0\n" +
                        "windDirection=180\n" +
                        "liftUp=0.0"},
                {"adminDOSBox", "DOSBox has been launched as administrator: \nplease either run it as standard user\n"
                        + "or run this trainer as administrator"},
                {"checkBoxLabel", "Extreme mode (set wind strength up to 99)"},
                {"returnButton", "Return"},
                {"detectVersion", "Detected DOSBox version: "},
                {"unsupportedDOSBox", "Supported DOSBox versions are: 0.73, 0.74\nDetected DOSBox version is: 0."},
                {"unknownException", "This should not happen - please write to me about this issue"}
        };
    }
}
