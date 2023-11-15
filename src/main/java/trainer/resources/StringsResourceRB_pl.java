package trainer.resources;

import java.util.ListResourceBundle;

@SuppressWarnings({"NewClassNamingConvention", "SpellCheckingInspection"})
public class StringsResourceRB_pl extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"topLabel", "Deluxe Ski Jump 2 Trainer autorstwa Mtik333"},
                {"detectButton", "Wykryj DOSBox z DSJ2"},
                {"windStrength", "Siła wiatru"},
                {"windDirection", "Kierunek wiatru (0 oznacza strzałkę w prawo)"},
                {"speedJumper", "Przyspiesz skoczka (domyślny klawisz: F11)"},
                {"liftJumper", "Podnieś skoczka (domyślny klawisz: F12)"},
                {"increaseWind", "Zwiększ wiatr o (domyślny klawisz: F6)"},
                {"decreaseWind", "Zmniejsz wiatr o (domyślny klawisz: F7)"},
                {"activate", "Aktywuj (domyślny klawisz: F9)"},
                {"deactivate", "Dezaktywuj (domyślny klawisz: F10)"},
                {"help", "Pomoc"},
                {"exit", "Wyjście"},
                {"message", "Komunikat"},
                {"noDOSBox", "Nie znaleziono procesu DOSBox.exe"},
                {"noDSJ2", "DOSBox uruchomiony, ale nie znaleziono uruchomionego DSJ2"},
                {"noDSJ2", "DOSBox uruchomiony, ale nie znaleziono uruchomionego DSJ2\n" +
                        "Proszę sprawdzić czy uruchomiona jest nakładka NVIDIA Shadowplay,\n" +
                        "jeśli tak, proszę wyłączyć ją i spróbowac ponownie.\n" +
                        "Jeśli to nie pomaga, proszę o kontakt."},
                {"dsj2Unloaded", "Nastapiło wyjście z gry, zatrzymuję trainer"},
                {"foundDSJ2", "Znaleziono uruchomiony DSJ 2.1!"},
                {"foundDSJ2P", "Znaleziono uruchomiony DSJ 2.1P!"},
                {"helpText", "Proszę przeczytać instrukcję pod adresem https://github.com/Mtik333/DSJ2Trainer\n\n" +
                        "1. Uruchom DOSBox\n2. Uruchom DSJ2 w DOSBoxie\n3. Naciśnij przycisk aby wykryć grę\n" +
                        "4. Ustaw wartości suwaków\n5. Naciśnij przycisk aktywacji\n6. Leć Adam leć jak najdalej"},
                {"saveValues", "Zapisz wartości"},
                {"loadValues", "Załaduj wartości"},
                {"activateHotkey", "Klawisz aktywacji"},
                {"deactivateHotkey", "Klawisz deaktywacji"},
                {"speedUpHotkey", "Klawisz szybkości"},
                {"liftUpHotkey", "Klawisz wysokości"},
                {"increaseWindHotkey", "Klawisz zmiany wiatru +"},
                {"decreaseWindHotkey", "Klawisz zmiany wiatru -"},
                {"changeHotkeys", "Zmień domyślne klawisze"},
                {"savedValuesSuccess", "Wartości zapisano pomyślnie"},
                {"loadedValuesSuccess", "Wartości wczytano pomyślnie"},
                {"thisIsDSJ2P", "Znaleziono wersję DSJ 2.1P, która nie jest obecnie wspierana"},
                {"loadingValuesError", "Problem z załadowaniem wartości z pliku, sprawdź format pliku." +
                        "\nPlik powinien wyglądać nastepująco: \n" +
                        "speedUp=0.0\n" +
                        "windStrength=3.0\n" +
                        "windDirection=180\n" +
                        "liftUp=0.0"},
                {"adminDOSBox", "DOSBox został uruchomiony jako administrator: \nproszę uruchomić go standardowym użytkownikiem\n"
                        + "albo uruchomić trainer jako administrator"},
                {"checkBoxLabel", "Tryb ekstremalny (siła wiatru w przedziale 0-99)"},
                {"returnButton", "Powrót"},
                {"detectVersion", "Wykryta wersja DOSBox: "},
                {"unsupportedDOSBox", "Wspierane wersje DOSBox to: 0.73, 0.74\nWykryta wersja DOSBox to: 0."},
                {"unknownException", "Jak do tego doszło - nie wiem. Napisz proszę do mnie, spróbuję sprawdzić dlaczego program nie działa"}
        };
    }
}
