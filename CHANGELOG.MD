# Lista zmian

Zmiany w kodzie dokumentowane tutaj:

## [1.4.1] - 02.07.2023
Bugfix:
- naprawiony problem z wyjątkiem blokującym uruchomienie programu gdy język systemu jest inny niż angielski lub polski;

## [1.4.0] - 15.05.2021
Nieco usprawnień na życzenie:
- dodane zwiększanie i zmniejszanie wiatru w trakcie gry (domyślnie za pomocą klawiszy F6 i F7);
- dodane menu, w którym istnieje możliwość zmiany klawiszy do wybranych operacji;

## [1.3.2] - 10.01.2021
Odrobinę poprawek:
- szukanie zaczynam od bazowego adresu 0x00400000;
- użyty jeszcze inny zestaw uprawnień niż poprzednio;
- dodana zapasowa klasa na wypadek zabawy z uprawnieniami w przyszłości;
- dodana informacja o możliwym problemie powodowanym przez nakładkę NVIDIA Shadowplay z prośbą kontaktu;
- w przypadku gdy tekst opisowy nie wyświetla się w całości, najechanie na element wyświetli tooltip z pełnym tekstem;
- zmodyfikowany plik FXML, aby interfejs wyświetlał się lepiej przy użytej skali 125%;
- klawisz F10 służy do aktywacji i deaktywacji programu;

## [1.3.1] - 04.05.2020
Poprawka związana z błędem odmowy dostępu, który objawiał się w braku reakcji programu na naciśnięcie przycisku wykrywającego DSJ2.

## [1.3.0] - 11.02.2020
To już chyba koniec:
- dodane wsparcie dla wersji 0.73 DOSBoxa;
- gdy zostanie wykryta inna wersja niż 0.73 lub 0.74, rzucany jest wyjątek informujący, jaka wersja została wykryta w pamięci;

## [1.2.5] - 09.02.2020
Nareszcie:
- dodana możliwość ustawiania silniejszego wiatru (w zakresie 0-99), oczywiście gra powyżej wartości 10 w zasadzie nie ogarnia i nie ma to specjalnego sensu, ale skoro stary trainer miał tę funkcjonalność, to w tym również będzie to możliwe;

## [1.2.1] - 07.02.2020
Drobne rzeczy:
- naprawiony problem z wersją "portable" DOSBoxa;
- jeśli DOSBox zostanie uruchomiony z uprawnieniami administratora, a trainer nie: pojawi się odpowiednie okno z treścią wyjątku; 

## [1.2.0] - 20.01.2020
Dodano:
- możliwość zapisywania lub wczytywania wartości z wewnętrznego pliku z ustawieniami;
- możliwość ustawienia poziomu logowania uruchamiając przez konsolę poleceń;
- wsparcie dla wersji 2.1P (z polskimi skoczniami);

## [1.1.5] - 09.01.2020
Kolejne poprawki:
- dodany timeout w przypadku natrafienia na ładowanie skoczni w pierwszym uruchomieniu, wiatr powinien się teraz zawsze ustawiać;
- dodane pola tekstowe do wpisania dowolnych wartości z dozwolonych zakresów;
- dalszy refactor pakietów i próba odhardkodowania długości i szerokości elementów w widoku;
- przesunięcie paru metod do klasy Helpera, żeby odciążyć nieco główny kontroler;

## [1.1.0] - 04.01.2020
Niby drobne poprawki, ale jednak coś tam się zmieniło:
- dodany resource bundle, żeby objaśnienia były po polsku i po angielsku na podstawie języka używanego w systemie;
- drobny refactor w kodzie m.in. przeniesienie wyjątków do oddzielnego pakietu;
- poprawka, która powinna wyeliminować problem z nieustawiającym się wiatrem za pierwszym załadowaniem którejkolwiek skoczni;
- dodane 4 kierunki wiatru (po "skosie");

## [1.0.0] - 02.01.2020

Pierwsza wersja programu udostępniona publicznie zawiera:
- podstawowy trainer do Deluxe Ski Jump 2;
- możliwość ustawienia 5 wartości siły wiatru (od 0 do 4);
- możliwość ustawienia 5 wartości kierunku wiatru (od 0 stopni do 360 stopni);
- możliwość ustawienia 5 wartości do "podnoszenia skoczka";
- możliwość ustawienia 5 wartości do "przyspieszania skoczka";
- obsługa wyjątków: braku włączonego DOSBoxa, DOSBoxa bez włączonego DSJ2, DSJ2 wyłączonego w trakcie działania programu;