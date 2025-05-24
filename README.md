# QBRSudoku

Un'app Android in Kotlin che mostra una griglia di Sudoku caricata dinamicamente da una API esterna, consentendo all'utente di giocare, visualizzare una preview in tempo reale della griglia e verificare la soluzione.

## Funzionalità principali

- **Caricamento dinamico**: la tabella Sudoku viene caricata da [sudoku-api.vercel.app](https://sudoku-api.vercel.app/api/dosuku).
- **Celle non modificabili**: i numeri forniti dall'API come iniziali non sono modificabili dall'utente.
- **Interazione semplice**: puoi inserire numeri tramite pulsanti o UI custom.
- **Preview in tempo reale**: sotto la griglia viene visualizzata una preview testuale aggiornata ad ogni modifica.
- **Verifica soluzione**: un pulsante permette di confrontare la soluzione inserita con quella dell’API.
- **Storico delle partite**: tutte le partite giocate vengono salvate in un database locale. Puoi vedere le partite passate, lo stato (completata o meno), la data e la soluzione.
- **Database locale**: utilizzo di Room/SQLite per la gestione dello storico delle partite.

## Screenshot

<img src="https://github.com/user-attachments/assets/317ea88e-b19d-45c7-b5e0-21a00d91f56e" width="250"/>
<img src="https://github.com/user-attachments/assets/5ca44bc2-b6a2-4138-b09d-18ebd28039d6" width="250"/>
<img src="https://github.com/user-attachments/assets/308bf064-2a82-4118-840c-b530ef8a6253" width="250"/>



## Come funziona

1. **All'avvio** viene fatta una chiamata HTTP all'API e la board viene visualizzata.
2. **I numeri diversi da 0** sono mostrati e non modificabili.
3. **Le celle vuote (0)** sono editabili dall’utente.
4. **La preview** mostra in tempo reale lo stato della board.
5. **Premi "Verifica"** per sapere se hai risolto correttamente il Sudoku.
6. **Ogni partita** (con soluzione, stato, data) viene salvata su database nello storico.
7. **Puoi accedere allo storico** delle partite giocate e vedere i dettagli di ciascuna.
## Struttura del progetto

- `MainActivity.kt`: Gestisce il ciclo di vita dell’app e le principali interazioni utente.
- `BoardView.kt`: Custom View per la visualizzazione e gestione della griglia Sudoku.
- `SudokuApi.kt`: Gestisce le chiamate REST verso l’API Sudoku.
- `GameHistoryActivity.kt`: Activity per la visualizzazione dello storico delle partite.
- `GameHistoryAdapter.kt`: Adapter per mostrare la lista delle partite giocate.
- `GameEntity.kt`, `GameDao.kt`, `GameDatabase.kt`: Strati dati del database (Room) per la gestione delle partite giocate.


## Come eseguire

1. Clona il repository:
    ```sh
    git clone https://github.com/davidbelfiori/testAPISUdoku.git
    ```
2. Apri il progetto in **Android Studio**.
3. Sincronizza le dipendenze (Gradle).
4. Esegui su un emulatore o dispositivo reale.

## Database & Storico

- Lo storico delle partite viene gestito tramite Room (ORM per SQLite).
- Ogni partita salvata contiene: data, stato, schema iniziale, schema soluzione, schema utente finale.
- Puoi visualizzare lo storico tramite una activity dedicata.

## API di riferimento

- [https://sudoku-api.vercel.app/api/dosuku](https://sudoku-api.vercel.app/api/dosuku)

## Autore

- [David Julian Belfiori](https://github.com/davidbelfiori)
- [Nicolò Bianchi](https://github.com/nich-bi)
- [Matteo Volpe](https://github.com/Fox070204)

