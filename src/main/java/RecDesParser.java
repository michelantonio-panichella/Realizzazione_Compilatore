package main.java;

import main.java.Lexer;

import java.util.*;


/*
 *       S-->Program EOF
 *       Program --> Stmt Program1
 *       Program1 --> ;Stmt Program1 | epsilon
 *       Stmt --> IF Expr THEN Stmt ELSE Stmt END IF | ID ASSIGN Expr | WHILE Expr LOOP Stmt END LOOP
 *       Expr --> ID Expr1 | NUMBER Expr1
 *       Expr1 --> Relop Expr Expr1 | epsilon
 *       RELOP --> OP_MIN | OP_MIN_EQ | OP_MIN_MAX | OP_MAX | OP_MAX_EQ
 * */

class RecDesParser {
    static int ptr;
    private static ArrayList<Token> tokenArrayList = new ArrayList<>();

    public static void main(String args[]) {
        Lexer lexer = new Lexer();
        String filepath = args[0];

        if (lexer.initialize(filepath)) {
            Token token;
            try {
                while ((token = lexer.nextToken()) != null) {
                    tokenArrayList.add(token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Token token = new Token("EOF");
        tokenArrayList.add(token);
        boolean isValid = S();
        if ((isValid) & (ptr == tokenArrayList.size() - 1)) {
            System.out.println("The input string is valid.");
        } else {
            System.out.println("Syntax Error");
        }
    }

    static boolean S() {
        int fallback = ptr;
        if (!Program()) {
            ptr = fallback;
            return false;
        }
        return true;
    }

    static boolean Program() {
        int fallback = ptr;
        if (!Stmt()) {
            ptr = fallback;
            return false;
        }
        if (!Program1()) {
            ptr = fallback;
            return false;
        }
        return true;
    }

    static boolean Program1() {
        int fallback = ptr;
        if (ptr < tokenArrayList.size() - 1) {
            if (tokenArrayList.get(ptr).getName().equals("Separatore")) {
                if (ptr < tokenArrayList.size() - 1) {
                    ptr = ptr + 1;
                    if (ptr == tokenArrayList.size()) {//Questo controllo permette di vedere se il punto e virgola è l'ultimo carattere da inserire... e se è l'ultimo restituisce vero poiché dopo il punto e virgola può esserci anche epsilon
                        return true;
                    } else {
                        if (!Stmt()) {
                            ptr = fallback;
                            return false;
                        }
                        if (!Program1()) {
                            ptr = fallback;
                            return false;
                        }
                    }
                } else {
                    ptr = fallback;
                    return false;
                }

            } else {
                return true;
            }
        } else if(tokenArrayList.get(ptr).getName().equals("EOF")){
            return true;
        }
        return true;
    }

    static boolean Stmt() {
        int fallback = ptr;
        if (tokenArrayList.get(ptr).getName().equals("IF")) {
            ptr = ptr + 1;
            if (!Expr()) {
                ptr = fallback;
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("THEN"))) {
                ptr = fallback;
                return false;
            }
            if (!Stmt()) {
                ptr = fallback;
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("ELSE"))) {
                ptr = fallback;
                return false;
            }
            if (!Stmt()) {
                ptr = fallback;
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("END"))) {
                ptr = fallback;
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("IF"))) {
                ptr = fallback;
                return false;
            }
            /*
            if (ptr < tokenArrayList.size() - 1) {
                ptr = ptr + 1;
            }*/
            return true;
        } else if (tokenArrayList.get(ptr).getName().equals("ID")) {
            ptr = ptr + 1;
            if (!(tokenArrayList.get(ptr++).getName().equals("ASSIGN"))) {
                ptr = fallback;
                return false;
            }
            if (!Expr()) {
                return false;
            }
            return true;
        } else if (tokenArrayList.get(ptr).getName().equals("WHILE")) {
            ptr = ptr + 1;
            if (!Expr()) {
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("LOOP"))) {
                ptr = fallback;
                return false;
            }
            if (!Stmt()) {
                return false;
            }

            if (!(tokenArrayList.get(ptr++).getName().equals("END"))) {
                ptr = fallback;
                return false;
            }
            if (!(tokenArrayList.get(ptr++).getName().equals("LOOP"))) {
                ptr = fallback;
                return false;
            }
            /*
            if (ptr < tokenArrayList.size() - 1) {
                ptr = ptr + 1;
            }*/
            return true;
        }
        return false;
    }

    static boolean Expr1() {
        int fallback = ptr;
        if (Relop()) {
            if (!Expr()) {
                ptr = fallback;
                return false;
            }
            if (!Expr1()) {
                ptr = fallback;
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    static boolean Expr() {
        int fallback = ptr;
        if (tokenArrayList.get(ptr).getName().equals("ID")) {
            int counter = ptr + 1;
            if (counter < tokenArrayList.size() - 1) { //effettua controllo se è finita o meno la stringa
                ptr = ptr + 1;
                if (!Expr1()) {
                    return false;
                }
            }
            return true;
        } else if (tokenArrayList.get(ptr).getName().equals("NUMB")) {
            int counter = ptr + 1;
            if (!(counter > tokenArrayList.size() - 1)) {
                ptr = ptr + 1;
                if (!Expr1()) {
                    return false;
                }
            }
            return true;
        }
        ptr = fallback;
        return false;
    }

    static boolean Relop() {
        int fallback = ptr;
        if (tokenArrayList.get(ptr).getName().equals("OP_MIN_EQ")) {
            ptr = ptr + 1;
            return true;
        }
        if (tokenArrayList.get(ptr).getName().equals("OP_MIN_MAX")) {
            ptr = ptr + 1;
            return true;
        }
        if (tokenArrayList.get(ptr).getName().equals("OP_MAX_EQ")) {
            ptr = ptr + 1;
            return true;
        }
        if (tokenArrayList.get(ptr).getName().equals("OP_MIN")) {
            ptr = ptr + 1;
            return true;
        }
        if (tokenArrayList.get(ptr).getName().equals("OP_MAX")) {
            ptr = ptr + 1;
            return true;
        }
        ptr = fallback;
        return false;
    }

}
