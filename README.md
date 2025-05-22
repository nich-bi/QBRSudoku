# testAPISUdoku

Un'app Android in Kotlin che mostra una griglia di Sudoku caricata dinamicamente da una API esterna, consentendo all'utente di giocare, visualizzare una preview in tempo reale della griglia e verificare la soluzione.

## Funzionalità principali

- **Caricamento dinamico**: la tabella Sudoku viene caricata da [sudoku-api.vercel.app](https://sudoku-api.vercel.app/api/dosuku).
- **Celle non modificabili**: i numeri forniti dall'API come iniziali non sono modificabili dall'utente.
- **Interazione semplice**: puoi inserire numeri tramite pulsanti o UI custom.
- **Preview in tempo reale**: sotto la griglia viene visualizzata una preview testuale aggiornata ad ogni modifica.
- **Verifica soluzione**: un pulsante permette di confrontare la soluzione inserita con quella dell’API.

## Screenshot

## Screenshot

<img src="https://github.com/user-attachments/assets/5622ab19-5d16-4d5b-8846-8d9380ecfe0e" width="350"/>


## Come funziona

1. **All'avvio** viene fatta una chiamata HTTP all'API e la board viene visualizzata.
2. **I numeri diversi da 0** sono mostrati e non modificabili.
3. **Le celle vuote (0)** sono editabili dall’utente.
4. **La preview** mostra in tempo reale lo stato della board.
5. **Premi "Verifica"** per sapere se hai risolto correttamente il Sudoku.

## Struttura del progetto

- `MainActivity.kt` - Activity principale, gestisce caricamento API e interazioni utente.
- `BoardView.kt` - Custom View che disegna e gestisce la griglia Sudoku.
- `SudokuApi.kt` - Classe per la chiamata REST all’API Sudoku.
- `activity_main.xml` - Layout principale, contiene BoardView, preview e controlli.

## Come eseguire

1. Clona il repository:
    ```sh
    git clone https://github.com/davidbelfiori/testAPISUdoku.git
    ```
2. Apri il progetto in **Android Studio**.
3. Sincronizza le dipendenze (Gradle).
4. Esegui su un emulatore o dispositivo reale.

## Personalizzazioni

- Puoi modificare i colori, lo stile della preview o aggiungere altre funzionalità nella `BoardView`.
- La preview testuale è facilmente sostituibile con una grafica a tabella se preferisci.

## API di riferimento

- [https://sudoku-api.vercel.app/api/dosuku](https://sudoku-api.vercel.app/api/dosuku)

## Autore

- [David Julian Belfiori](https://github.com/davidbelfiori)
- [Nicolò Bianchi](https://github.com/nich-bi)
- [Matteo Volpe](https://github.com/Fox070204)

