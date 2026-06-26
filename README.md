# PRG2_pipeline
Es handelt sich um eine einfache Datenverarbeitungspipeline in Kotlin, welche mithilfe funktionaler Programmierkonzepte Log-Zeilen auswertet.

# Technische Dokumentation
Das Programm besteht aus den Klassen LogEntry, LogLevel, LogFilter und LogParser.
Dabei implementiert der *LogFilter* die Stufen 1 und 2 aus der Aufgabenstellung. Der *LogParser* implementiert die Stufe 3.
*LogEntry* und *LogLevel* wurden in eigene Klassen ausgelagert, denn sie dienen zur Modellierung eines Log-Eintrags an sich und werden sowohl in *LogFilter* als auch in *LogParser* verwendet.

Jede öffentliche Methode in dieser Dokumentation enthält eine Detail-Beschreibung. Hierbei wird jeder Verarbeitungsschritt im imperativen Stil beschrieben (wie wenn es sich um eine klassische Schleife handeln würde). Diese Form der Beschreibung dient hier als vertraute Sprache, um zu demonstrieren, dass die funktionalen Programmierkonzepte verstanden wurden.

## LogFilter
### filterLogByLevel
Die Methode setzt Stufe 1 aus der Aufgabenstellung um, indem sie den gegebenen *log* nach dem gegebenen *level* filtert.

Aus dem gegebenen Log (einer Liste aus Strings) werden zunächst nur die Log-Zeilen ausgewählt, die mit der gegebenen Level-Angabe beginnen (z.B: "INFO:").
Im zweiten Schritt wird aus jeder Log-Zeile die eigentliche Nachricht ausgewählt (die Level-Angabe  wird hier nicht mehr gespeichert).
Die ausgewählten Nachrichten werden wiederum als Liste aus Strings zurückgegeben.

#### Detail-Beschreibung
```kotlin
log.filter { it.startsWith(level.text) }
```
- Erstelle eine neue (leere) String-Liste
- Für jeden String *it* in der Liste *log*:
    - Überprüfe, ob *it* mit *level.text* beginnt
    - Wenn ja, dann speichere *it* in der neuen String-Liste
- Gib die neue String-Liste zurück

```kotlin
.map { it.substring(level.text.length).trimStart() }
```
- Erstelle eine neue (leere) String-Liste
- Für jeden String *it* in der Liste, auf welche *map* angewendet wird:
    - Erstelle einen Sub-String, der beim Index *level.text.length* des Strings *it* beginnt
    - Entferne alle Leerzeichen am Beginn des Sub-Strings
    - Speichere den Sub-String in der neuen String-Liste
- Gib die neue String-Liste zurück

Es handelt sich bei der Variable *level* in den oben beschriebenen Zeilen jeweils um eine Closure, da die Variable aus dem Kontext der Methode *filterLogByLevel* bekannt ist.

#### Bezug zur enum class LogLevel
Jedes einzelne Log-Level wird nicht etwa als String repräsentiert, sondern als Teil der enum class *LogLevel*, welche z.B. dem Eintrag *INFO* den Text *"INFO:"* zuordnet. Der *level.text* enthält also immer eine korrekt formatierte Level-Angabe, die mit einem Doppelpunkt endet.

### getLogLevelCount
Die Methode setzt Stufe 2 aus der Aufgabenstellung um, indem sie die Einträge des gegebenen *log* nach ihrem Level gruppiert und die Anzahl der Einträge pro Level zählt.
Das Endergebnis wird als Map zurückgegeben, welche jedem LogLevel eine Ganzzahl (nämlich die Anzahl der Einträge) zuordnet.
Aus der Gestaltung der enum class *LogLevel* geht hervor, dass der Schlüssel der Map vom Typ *LogLevel* sein muss, nicht vom Typ *String*.

#### Detail-Beschreibung
```kotlin
LogLevel.entries.associateWith
```
- Erstelle eine Liste mit allen Einträgen d.h. *entries* der enum class LogLevel
- Erstelle eine Map, für die gilt:
    - Die Schlüssel setzen sich aus den *entries* zusammen
    - Die Werte werden anhand einer Funktion *valueSelector* berechnet, die das aktuelle Element aus *entries* selbst als Eingabeparameter verwendet
      (Der *valueSelector* wird im nächsten Schritt beschrieben.)
- Gib die erstellte Map zurück

```kotlin
{ level -> log.count { it.startsWith(level.text) } }
```
Dieser Abschnitt beschreibt den *valueSelector* für den obigen Aufruf von *associateWith*. Das *level* ist das aktuelle Element aus der Liste *LogLevel.entries*. Der *log* ist die Liste aus Strings, welche an die Methode *getLogLevelCount* übergeben wurde.

Der *valueSelector* nimmt das *level* als Eingabeparameter, ruft *log.count* auf und gibt dessen Rückgabewert zurück. Es handelt sich bei der Variable *log* um eine Closure, da diese aus dem Kontext der Methode *getLogLevelCount* bekannt ist.
Das Predicate ganz am Ende der Zeile, welches an *log.count* übergeben wird, nutzt selbst das *level* als Closure, da das *level* aus dem Kontext des *valueSelector* bekannt ist.

Der Ablauf in *log.count* kann wie folgt verstanden werden:
- Erstelle eine Variable *count*, die bei 0 beginnt
- Für jeden String *it* in der Liste *log*:
    - Überprüfe, ob *it* mit *level.text* beginnt
    - Wenn ja, dann erhöhe die Variable *count* um eins
- Gib die Variable *count* zurück

Der obige Aufruf von *associateWith* garantiert bereits, dass der berechnete *count* als Wert dem richtigen *level* als Schlüssel zugeordnet wird, wenn die finale Map erstellt wird.

#### Bezug zur enum class LogLevel
Da das Log-Level als enum class repräsentiert wird, können alle möglichen Log-Level einfach als *LogLevel.entries* ausgegeben werden. Dies wäre nicht möglich, wenn das Log-Level als einfacher String modelliert wäre, da es dann unendlich viele mögliche Werte gäbe.

## LogParser
### getValidLines
Die Methode setzt Stufe 3 aus der Aufgabenstellung teilweise um, indem sie die Ergebnisse der Parsing-Funktion *parseLog* in einer Pipeline verarbeitet, die *map* und *mapNotNull* kombiniert.
Die Methode gibt einen String zurück, welcher alle korrekten Log-Zeilen enthält (mit *\n* getrennt). Fehlerhafte Zeilen werden ignoriert.

#### Detail-Beschreibung

```kotlin
        return log
            .map { parseLog(it) }
            .mapNotNull { it.getValueOrNull() }
            .joinToString("\n")
```
##### map
- Erstelle eine neue (leere) Result-Liste, d.h. eine Liste des Typs `List<Result<LogEntry>>`
- Für jeden String *it* in der Liste *log*:
    - Wende die Methode *parseLog* an, die `Result.Success<LogEntry>` oder `Result.Failure` zurückgibt
    - Speichere das Ergebnis in der Result-Liste
- Gib die Result-Liste zurück
##### mapNotNull
- Erstelle eine neue (leere) Log-Entry-Liste, d.h. eine Liste des Typs `List<LogEntry>`
- Für jedes Result *it* in der Result-Liste:
    - Wende dessen Methode *getValueOrNull* an, die bei `Result.Success<LogEntry>` den *LogEntry* zurückgibt, bei `Result.Failure` aber den Wert *null* zurückgibt.
    - Wenn das Ergebnis nicht *null* ist, dann speichere es in der Log-Entry-Liste.
- Gib die Log-Entry-Liste zurück
##### joinToString
- Erstelle einen neuen (leeren) String, in dem das Endergebnis gespeichert werden soll
- Für jeden LogEntry in der Log-Entry-Liste:
    - Ist der LogEntry nicht das erste Element in der Liste, dann hänge *\n* an das Endergebnis an
    - Wende die Methode *toString* des LogEntry an
    - Hänge den resultierenden String an das Endergebnis an
- Gib das Endergebnis zurück

#### Bezug zur data class LogEntry
In der Klasse *LogEntry* wird sowohl das zugehörige *LogLevel* als auch der Inhalt (*message*) der gesamten Log-Zeile gespeichert. Die Methode *toString* in *LogEntry* gibt direkt die *message* zurück. Dies ermöglicht das Umwandeln der Elemente der Log-Entry-Liste in Strings ohne weiteren Zwischenschritt, da *joinToString* automatisch die Methode *toString* der einzelnen Elemente aufruft.


### getLineDescriptions
Die Methode setzt Stufe 3 aus der Aufgabenstellung teilweise um, indem sie die Ergebnisse der Parsing-Funktion *parseLog* in einer Pipeline verarbeitet. Dabei werden *map* und *mapIndexed* kombiniert.
Die Methode gibt einen String zurück, welcher eine Beschreibung sämtlicher Log-Zeilen enthält (mit *\n* getrennt). Die Beschreibung enthält die Zeilennummer und den kompletten Inhalt der Log-Zeile, im Fehlerfall aber die Zeilennummer und den Text `<<<Fehler>>>`.

#### Detail-Beschreibung
Hier wird nur die Zeile mit *mapIndexed* beschrieben, da sich der Rest der Methode nicht von *getValidLines* unterscheidet.

```kotlin
.mapIndexed { index, result -> result.toLineDescription(index) }
```
- Erstelle eine neue (leere) String-Liste
- Für jedes Element *result* in der Result-Liste:
    - Speichere den Index des aktuellen Elements
    - Übergebe den Index an die Methode *toLineDescription* des aktuellen Elements.
    - Speichere den resultierenden String in der String-Liste
- Gib die String-Liste zurück

### getLineDescriptions (Überladung mit Parameter *amount*)
Diese Überladung dient einzig zur Demonstration eines möglichen Anwendungsfalls einer *Sequence* (siehe optionalen Punkt in Stufe 3).

Der *log* wird zu Beginn in eine Sequence umgewandelt und es werden nur die ersten *amount* Elemente aus dem Ergebnis von *mapIndexed* ausgewählt.
Da eine Sequence nach dem Lazy-Prinzip funktioniert, folgt daraus: Möchte man von 100000 Log-Zeilen nur die ersten 10 Zeilen auslesen, dann werden tatsächlich nur die ersten 10 Zeilen verarbeitet.
Bei einer List würden zunächst alle 100000 Log-Zeilen verarbeitet werden und die Funktion *take* würde anschließend die ersten 10 Zeilen auswählen.


# Reflexion
## Designentscheidungen
### LogLevel
Zur Darstellung eines Log-Levels habe ich die enum class *LogLevel* eingeführt, anstatt das Log-Level direkt als String zu repräsentieren.

Ein Vorteil dieses Ansatzes ist, dass man garantieren kann, dass dem gegebenen *level* immer ein korrekt formatierter Text zugeordnet wurde. Dies stellt vor allem sicher, dass *level.text* immer mit einem Doppelpunkt endet, welcher als Trennzeichen unbedingt benötigt wird. Die Suche nach einem Doppelpunkt in der Methode *filterLogByLevel* ist somit gar nicht mehr nötig.

Der größte Vorteil ist jedoch, dass man die *LogLevel.entries* einfach als Liste abfragen kann, was das Gruppieren bzw. das Finden von Log-Leveln erleichtert.

Ein möglicher Nachteil dieses Ansatzes ist die Redundanz, da man jedem Log-Level (z.B. INFO) manuell einen String mit Doppelpunkt zuordnen muss (z.B. "INFO:"). Gäbe es sehr viele Log-Level, dann könnte man sich dabei leicht verschreiben bzw. einen Copy-Paste-Fehler begehen. In diesem Fall gibt es aber nur drei verschiedene Log-Level.

### LogEntry
Zur Modellierung eines Log-Eintrags habe ich mich entschieden, neben dem *LogLevel* den gesamten Inhalt der Log-Zeile als String *message* zu speichern. Der Grund dafür ist, dass die Klasse *LogEntry* nur im *LogParser* benötigt wird (Stufe 3) und dort keine Funktionalität gefragt ist, welche den Log-Level-Text (z.B: "INFO:") von der eigentlichen Nachricht trennen müsste. Das Gegenteil ist der Fall: Es ist sehr nützlich, die gesamte *message* mit der Methode *toString* des *LogEntry* zurückgeben zu können.

### LogParser
Ich habe mich entschieden, alle Funktionen bis auf die drei beschriebenen als *private* zu markieren und die zusätzlich benötigte Klasse *Result* als private nested class im *LogParser* zu verbergen. Da die öffentlichen Funktionen nur mit Strings bzw. String-Listen arbeiten, muss die Existenz der Klasse *Result* nach außen gar nicht bekannt sein.

Innerhalb der class *Result* habe ich vor allem zwei nicht offensichtliche Entscheidungen getroffen:
- Result.Failure ist ein object, keine data class. Da in meinem ersten Entwurf ohnehin nur eine Standard-Fehlermeldung in der (damals noch) data class Result.Failure gespeichert wurde, erschien es mir plausibler, diese Standard-Fehlermeldung stattdessen von der Methode *toString* in Result.Failure generieren zu lassen.
- Die Methode *getValueOrNull* habe ich hinzugefügt, um die Verwendung von *mapNotNull* in *getValidLines* zu ermöglichen bzw. um die Übersichtlichkeit in *getValidLines* zu verbessern.

## Pure Funktionen
Alle Funktionen in meiner Lösung sind pure Funktionen. Schließlich ging aus der Aufgabenstellung hervor, dass wir pure Funktionen verwenden sollen. Da letztendlich "nur" eine Liste aus Strings in irgendeine Ausgabe umgewandelt werden musste, d.h. es sich um eine reine Transformation von Daten handelte, war dies auch gut umsetzbar.

## Über map-Funktionen
Ich habe *map*, *mapNotNull* und *mapIndexed* verwendet und in der technischen Dokumentation ausführlich beschrieben. Ich habe keinen Anwendungsfall für *flatMap* gefunden, da kein Teil meiner Pipeline eine verschachtelte Liste zurückgeben muss. Auch *forEach* kommt in meiner Pipeline nicht vor. Letzteres wurde auch in der Aufgabenstellung nicht erwähnt.

## Behandlung von Fehlern
Es war aus der Aufgabenstellung klar, dass Fehler explizit als Daten modelliert werden sollten. Daher gibt es die sealed class *Result* mit *Result.Success* und *Result.Failure*. Auf diese bin ich oben bereits eingegangen.

Möchte man eine präzise Fehlermeldung ausgeben, dann müsste *Result.Failure* ebenfalls als data class modelliert werden, welche die Fehler speichert. Ich bin aber der Ansicht, dass der generische Fehler-String ausreicht (*Result.Failure.toString*). Zusammen mit den Zeilennummern aus *getLineDescriptions* gibt dieser ausreichendes Feedback, um fehlerhafte Log-Zeilen beheben zu können.

## KI-Tools
Alle öffentlichen Funktionen wurden zunächst von Claude Haiku 4.5 erstellt und dann im Zusammenspiel mit der KI optimiert, um:
- Sie möglichst gut lesbar zu machen
- Sie so zu schreiben, dass ich jede Zeile zu 100% verstehe

Die Modellierung von *LogEntry* und *LogLevel* habe ich mir selbst ausgedacht. Den grundsätzlichen Aufbau der sealed class *Result* bzw. der in Stufe 3 geforderten Methode *parseLog* habe ich aus dem letzten Projekt kopiert und nachher abgeändert.

