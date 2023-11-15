# Changelog

Changes documented here:

## [1.4.1] - 02.07.2023
Bugfix:
- fixed issue with exception stopping program when locale is different than EN or PL

## [1.4.0] - 15.05.2021
A bunch of improvements as requested:
- added possibility to increase and decrease wind strength on fly without deactivating the trainer (default hotkeys: F6 and F7);
- added menu to set hotkeys for all possible operations;

## [1.3.2] - 10.01.2021
Bunch of fixes
- searching starts with base address 0x00400000
- another set of permissions used in code
- added class PrivilegeTest in case of future tricks with permissions
- added info about NVIDIA Shadowplay issues and kind request of contact
- if text on a label is too long, hovering on it will show full text
- modified FXML file for case of 125% scale used, as interface cut some labels and texts
- added F10 as hotkey to activate/deactivate trainer

## [1.3.1] - 04.05.2020
Fix for issue with "access denied" that I found while testing Win7 VM. Due to that, nothing happened when button 
was clicked and exception was not displayed, so it was really confusing user who could experience that.

## [1.3.0] - 11.02.2020
Looks like this is the end
- added support for DOSBox 0.73 (DSJ2 is often bundled with this portable version)
- once version different than 0.73/0.74 is detected, exception is being thrown with info, which version has been detected

## [1.2.5] - 09.02.2020
Finally
- added possibility to set stronger wind (between 0 and 99), obviously game works weirdly when wind is over 10, 
  so it does not make sense to play with such wind strength, but since old trainer was able to set such values, 
  my will do it as well

## [1.2.1] - 07.02.2020
Small things
- fixed issue with portable DOSBox
- if DOSBox is launched with admin privileges, but trainer isn't launched admin privileges, 
  a message box with exception appears

## [1.3.0] - 11.02.2020
End of the line
- added support for DOSBox 0.73
- when version launched is neither 0.73 nor 0.74, throwing exception about version found in memory

## [1.2.0] - 19.01.2020
Added:
- ability to save/load values via internal property file
- ability to define logging value when launching via command line
- support for version 2.1P (with polish hills)

Plan for 1.3.0 is to support DOSBox on Linux distributions.

Plan for 1.4.0 is to allow any wind strength (bigger than 4).

## [1.1.5] - 09.01.2020
Further fixes
- added timeout in case of encountering loading hills on first launch, wind should be set always no
- added text fields to allow input of any value in possible range
- further refactor of packages, unhardcoding length and width of elements in view
- moved few methods to helper class to reduce length of main controller

## [1.1.0] - 04.01.2020
Some small fixes but enough to consider as new version
- added resource bundle to have localization in Polish
- small refactor in code including moving exceptions to different package
- fix for issue with trainer not propagating wind strenght/direction values during very first load of any hill
- added 4 wind directions (diagonal ones)

Plan for 1.2.0 is to allow any input of acceptable values of wind strength, wind direction, lifting up and speeding up; also setting logging level and loading/saving values of sliders

Plan for 1.3.0 is to support DOSBox on Linux distributions.

Plan for 1.4.0 is to allow any wind strength (bigger than 4).

## [1.0.0] - 02.01.2020

Initial release version, includes:
- basic trainer for Deluxe Ski Jump 2
- ability to set one of 5 wind strength values (0, 1, 2, 3, 4)
- ability to set one of 5 wind direction values (0, 90, 180, 270, 360 degrees)
- ability to set one of 5 lifting-up values
- ability to set one of 5 speeding-up values
- handling exceptions: DOSBox not launched, DSJ2 not launched within DOSBox, DSJ2 unloaded while trainer is activated
