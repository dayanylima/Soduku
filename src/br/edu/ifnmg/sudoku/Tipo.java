package br.edu.ifnmg.sudoku;

public class Tipo {

    private Integer grau;
    private Integer numLinhasBloco;
    private Integer numColunasBloco;

    public Tipo(Integer grau, Integer numLinhasBloco, Integer numColunasBloco) {
        this.grau = grau;
        this.numLinhasBloco = numLinhasBloco;
        this.numColunasBloco = numColunasBloco;
    }

    public Integer getGrau() {
        return grau;
    }

    public void setGrau(Integer grau) {
        this.grau = grau;
    }

    public Integer getNumLinhasBloco() {
        return numLinhasBloco;
    }

    public void setNumLinhasBloco(Integer numLinhasBloco) {
        this.numLinhasBloco = numLinhasBloco;
    }

    public Integer getNumColunasBloco() {
        return numColunasBloco;
    }

    public void setNumColunasBloco(Integer numColunasBloco) {
        this.numColunasBloco = numColunasBloco;
    }

}
