The flutter app flow is that
clicking on heart rate card : navs to a video record screen, where start recording enables an automatic 45 sec recording which user can delete and redo.
The respitory card does same but with audio.
Symptoms card navs to a multi-select screen with bunch of options generated from a external static config array.

when user creates a new entry and saves for any three card, a progress card is automatically displayed above the two cards row: "Current recording session" which allows user to see which of cards they already created

when user saves all three things, and click create new record button on the health monitor screen: a sqlite record is created in table with link to video, audio and json of systems.

---
if there are records in sqlite table, the area below the button serves as the preview of recorded info

Now without code, tell me how many screens are  there: including view screen, record screen, editable preview, persistent preview, and the method for create+fetch+insert in sqlite

----
in flutter, I created four tables: one each of card, and one for recording session. that way, I could track what card was inserted. can I do same here?
---

lets get started with recording interface and multi select+save interface along with navs. we can apply the save to db later.
First video recording screen.

a video camera preview area
and a timer of m/n sec where m is video length captured, and n is 45 sec

and a start capture button.
on tap on start capture, disable the button and start timer. when timer finishes, stop recording. and enable playback with delete option