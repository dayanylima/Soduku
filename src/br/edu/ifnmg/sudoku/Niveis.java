package br.edu.ifnmg.sudoku;

public class Niveis {

    private String nivel;
    private Integer maxIteracoes;

    public Niveis(String nivel, Integer maxIteracoes) {
        this.nivel = nivel;
        this.maxIteracoes = maxIteracoes;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Integer getMaxIteracoes() {
        return maxIteracoes;
    }

    public void setMaxIteracoes(Integer maxIteracoes) {
        this.maxIteracoes = maxIteracoes;
    }

}
