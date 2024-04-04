package com.example;

import java.util.ArrayList;
import java.util.List;

public class Documento {
    
    String nome;
    String tipo;
    Integer numero;
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public Integer getNumero() {
        return numero;
    }
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Documento(String nome, String tipo, Integer numero) {
        this.nome = nome;
        this.tipo = tipo;
        this.numero = numero;
    }

    public Documento() {
    }

    // Metodo per generare un documento con valori random usando la classe Random
    public static Documento generaDocumentoRandom() {
        Documento documento = new Documento();
        documento.setNome("Documento " + (int) (Math.random() * 100));
        documento.setTipo("Tipo " + (int) (Math.random() * 100));
        documento.setNumero((int) (Math.random() * 100));
        return documento;
    }

    @Override
    public String toString() {
        return "Documento [nome=" + nome + ", numero=" + numero + ", tipo=" + tipo + "]";
    }
    public ArrayList<Documento> generaDocumentiRandom(int i) {
        List<Documento> documenti = new ArrayList<Documento>();

        for (int j = 0; j < i; j++) {
            documenti.add(generaDocumentoRandom());
        }

        return (ArrayList<Documento>) documenti;
        
        
    }




}
