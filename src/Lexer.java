import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Lexer {

    public FileReader input;
    public BufferedReader bufferedReader;
    private static HashMap<String, Token> stringTable;  // la struttura dati potrebbe essere una hash map
    private int state;
    boolean ritornaUltimoCarattere = false;
    private boolean precedente;
    //..

    public Token installID(String lessema) {
        Token token;

        //utilizzo come chiave della hashmap il lessema
        if (stringTable.containsKey(lessema))
            return stringTable.get(lessema);
        else {
            token = new Token("ID", lessema);
            stringTable.put(lessema, token);
            return token;
        }
    }

    public void retrack() throws IOException {
        bufferedReader.reset();
    }

    public Lexer() {
        // la symbol table in questo caso la chiamiamo stringTable
        stringTable = new HashMap<String, Token>();
        state = 0;
        stringTable.put("if", new Token("IF"));   // inserimento delle parole chiavi nella stringTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza). IF poteva anche essere associato ad una costante numerica
        stringTable.put("then", new Token("THEN"));
        stringTable.put("while", new Token("WHILE"));
        stringTable.put("else", new Token("ELSE"));
        stringTable.put("int", new Token("INT"));
        stringTable.put("float", new Token("FLOAT"));
        stringTable.put("loop", new Token("LOOP"));
        stringTable.put("end", new Token("END"));
    }

    public Boolean initialize(String filePath) {
        try {
            input = new FileReader(filePath);
            bufferedReader = new BufferedReader(input);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }


        // prepara file input per lettura e controlla errori

    }

    public Token nextToken() throws Exception {


        state = 0;
        String lessema = "";//è il lessema riconosciuto
        char c;
        boolean vero = true;
        String str = " ";
        while (vero) {

            int letto = bufferedReader.read();
            if(letto == -1) {
                if ((ritornaUltimoCarattere == false)) {
                    vero = false;
                } else if (ritornaUltimoCarattere == true){
                    vero = true;
                }
            }
            c = (char)letto;


            //id
            switch (state) {
                case 0:
                    if (Character.isWhitespace(c)) {
                        state = 0;
                    } else if (Character.isLetter(c)) {
                        state = 1;
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                    } else if (Character.isDigit(c) && (Integer.parseInt(Character.toString(c)) == 0)) {
                        lessema = lessema + c;
                        state = 17;
                        bufferedReader.mark(1);
                    } else if (Character.isDigit(c)) {
                        state = 3;
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                    }
                     else if (c == '<') {
                        lessema = lessema + c;
                        state = 6;
                        bufferedReader.mark(1);
                    } else if (c == '>') {
                        lessema = lessema + c;
                        state = 12;
                        bufferedReader.mark(1);
                    } else if (/*c == '(' || c == ')' || c == '{' || c == '}' || c == ',' ||*/ c == ';') {
                        lessema = lessema + c;
                        state = 15;
                        bufferedReader.mark(1);
                    } else if(c=='"'){
                        state = 16;
                    } else {
                        if(letto!=-1) {
                            lessema = lessema + c;
                            Token token = new Token("SIMBOLO NON RICONOSCIUTO", lessema);
                            return token;
                        }else{
                            return null;
                        }
                    }
                    break;
                case 1:
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        state = 1;
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                    } else if (c == ' ' || c == '\n' || c == '_') {
                        state = 2;
                    } else if(c == ';'){
                        retrack();
                        return installID(lessema);
                    }else if(letto == -1){ // Controllo Fine File
                        return installID(lessema);
                    }
                    else {
                        retrack();
                        return installID(lessema);
                    }
                    break;
                case 2:
                    vero = true;
                    retrack();
                    return installID(lessema);
                case 3:
                    if (Character.isDigit(c)) {
                        lessema = lessema + c;
                        state = 3;
                        bufferedReader.mark(1);
                    } else if (Character.isWhitespace(c)) {
                        state = 5;
                    } else if(letto == -1) { //Controllo fine file, restituisce quello che c'era fino ad adesso
                        Token tok_Numb = new Token("NUMB",lessema);
                        return tok_Numb;
                    } else if(c == '.'){
                        // l'aggiunta del punto viene fatta nello stato 4
                        state = 4;
                    } else {
                        retrack();
                        Token tok_Numb = new Token("NUMB",lessema);
                        return tok_Numb;
                    }
                    break;
                case 4:
                    if (Character.isDigit(c)) {
                        lessema = lessema + '.';  //E' stato aggiunto qui in modo tale da effettuare un controllo dell'erroee poiché se veniva aggiunto prima, e si verificava che dopo il punto non c'era nulla non si era in grado di toglierno
                        lessema = lessema + c;
                        state = 18; //Attenzione lo stato 18 è il successivo (è messo cosiì in quanto è una correzione fatta dopo)
                        bufferedReader.mark(1);
                    } else if (c == ' ' || c == '\n') {
                        retrack();
                        Token tok_Numb2 = new Token("NUMB",lessema);
                        return tok_Numb2;
                    } else if(letto == -1) {
                        vero = true;
                        retrack();
                        Token tok_Numb2 = new Token("NUMB",lessema);
                        return tok_Numb2;
                    } else{
                        retrack();
                        Token tok_Numb3 = new Token("NUMB",lessema);
                        return tok_Numb3;
                    }
                    break;
                case 18: //
                    if (Character.isDigit(c)) {
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                    } else if (c == ' ' || c == '\n') {
                        state = 5;
                        //retrack();
                        //Token tok_Numb2 = new Token("NUMB", lessema);
                        //return tok_Numb2;
                    } else if(letto == -1) {
                        retrack();
                        Token tok_Numb2 = new Token("NUMB",lessema);
                        return tok_Numb2;
                    } else {
                        retrack();
                        Token tok_Numb3 = new Token("NUMB",lessema);
                        return tok_Numb3;
                    }
                    break;
                case 5:
                    retrack();
                    Token tok_Numb = new Token("NUMB",lessema);
                    return tok_Numb;
                case 6:
                    if (c == '=') {
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                        state = 7;
                    } else if (c == '>') {
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                        state = 8;
                    } else if (c == '-') {
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                        state = 10;
                    } else if(letto == -1){
                        Token token = new Token("OP_MIN", lessema);
                        return token;
                    } else {
                        state = 9;
                    }
                    break;
                case 7:
                    retrack();
                    Token to_OP_MIN_EQ = new Token("OP_MIN_EQ", lessema);
                    return to_OP_MIN_EQ;
                case 8:
                    retrack();
                    lessema = lessema +c;
                    Token to_OP_MIN_MAX = new Token("OP_MIN_MAX", lessema);
                    return to_OP_MIN_MAX;
                case 9:
                    retrack();
                    Token to_OP_MIN = new Token("OP_MIN", "<");
                    return to_OP_MIN;
                case 10:
                    if (c == '-') {
                        lessema = lessema + c;
                        state = 11;
                        bufferedReader.mark(1);
                    } else {
                        retrack();
                        Token token_assegnamento = new Token("ERRORE", lessema);
                        return token_assegnamento;
                    }
                case 11:
                    retrack();
                    Token token_assegnamento = new Token("ASSIGN", lessema);
                    return token_assegnamento;
                case 12:
                    if (c == '=') {
                        lessema = lessema + c;
                        bufferedReader.mark(1);
                        state = 13;
                    } else if(letto == -1) {
                        Token token = new Token("OP_MAX", lessema);
                        return token;
                    }else {
                        state = 14;
                    }
                    break;
                case 13:
                    retrack();
                    Token to_MAX_EQ = new Token("OP_MAX_EQ", ">=");
                    return to_MAX_EQ;
                case 14:
                    retrack();
                    Token token = new Token("OP_MAX", lessema);
                    return token;
                case 15:
                    retrack();
                    Token token_Separatore = new Token("Separatore", lessema);
                    return token_Separatore;
                case 17:
                    if(c == '.'){
                        state = 4;
                    }
                    retrack();
                    Token tokenNumZ = new Token("NUMB", lessema);
                    return tokenNumZ;
                case 16:
                    if(c!='"'){
                        lessema = lessema + c;
                    } else if(c == '\n'){
                        Token tokenError = new Token ("ERRORE", "lessema");
                        return tokenError;
                    } else if(c=='"'){
                        Token tokenCost = new Token("Costante", lessema);
                        return tokenCost;
                    }
            }
        }
        return null;
    }
}



