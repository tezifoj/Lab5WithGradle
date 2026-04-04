package org.example.utility;

public class StandardConsole implements Console {
    private static final String P = "$ ";
    private java.util.Scanner fileScanner = null;
    private final java.util.Scanner defScanner = new java.util.Scanner(System.in);

    public void print(Object obj) {
        System.out.print(obj);
    }

    public void println(Object obj) {
        System.out.println(obj);
    }

    public void printError(Object obj) {
        System.err.println("Error: " + obj);
    }

    public String readln() {
        try {
            if (fileScanner != null) {
                return fileScanner.hasNextLine() ? fileScanner.nextLine() : null;
            }
            if (!defScanner.hasNextLine()) {
                return null;
            }
            return defScanner.nextLine();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isCanReadln() {
        try {
            if (fileScanner != null) {
                return fileScanner.hasNextLine();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void printTable(Object elementLeft, Object elementRight) {
        System.out.printf(" %-35s%-1s%n", elementLeft, elementRight);
    }

    public void prompt() {
        if (fileScanner == null && !Thread.currentThread().isInterrupted()) {
            System.out.print(P);
            System.out.flush();
        }
    }

    public String getPrompt() {
        return P;
    }

    public void selectFileScanner(java.util.Scanner scanner) {
        this.fileScanner = scanner;
    }

    public void selectConsoleScanner() {
        this.fileScanner = null;
    }
}