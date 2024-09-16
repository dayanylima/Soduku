package br.edu.ifnmg.sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Sudoku {

    static Integer[] corVertice;
    static Integer[][] matrizAdjacencia;
    static Integer numVertices;

    static Integer grauSudoku;

    static Integer HEURISTIC_BREADTH_FIRST_SEARCH_SATURATED = 1;
    static Integer HEURISTIC_BREADTH_FIRST_SEARCH = 2;
    static Integer HEURISTIC_DEPTH_FIRST_SEARCH = 3;

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        // Tipos de Sudoku com grau e dimensões das submatrizes
        List<Tipo> tipos = Arrays.asList(
                new Tipo(4, 2, 2),
                new Tipo(6, 2, 3),
                new Tipo(8, 4, 2),
                new Tipo(9, 3, 3)
        );

        // Níveis de dificuldade com número máximo de iterações
        List<Niveis> niveis = Arrays.asList(
                new Niveis("easy", 15000),
                new Niveis("medium", 30000),
                new Niveis("difficult", 45000)
        );

        // Para cada tipo de Sudoku, executar os testes para cada nível
        tipos.forEach(sudoku -> {
            imprimirSeparadorSudoku(sudoku.getGrau());

            grauSudoku = sudoku.getGrau();
            numVertices = grauSudoku * grauSudoku;

            // Gerar matriz de adjacência para o Sudoku atual
            matrizAdjacencia = gerarMatrizAdjacencia(grauSudoku, sudoku.getNumLinhasBloco(), sudoku.getNumColunasBloco());

            // Executar os testes para cada nível de dificuldade
            niveis.forEach(nivel -> executarTestesPorNivel(sudoku, nivel));
        });
    }

    // Função auxiliar para imprimir separador do Sudoku atual
    private static void imprimirSeparadorSudoku(int grau) {
        String linha = "-".repeat(44);  // Separador
        System.out.println(String.format("\n%s\nSudoku %dX%d\n%s", linha, grau, grau, linha));
    }

    // Executa os testes de todos os algoritmos para um determinado nível de
    // dificuldade
    private static void executarTestesPorNivel(Tipo sudoku, Niveis nivel) {
        System.out.println(String.format("\n--> %s NIVEL".toUpperCase(), nivel.getNivel()));

        int maxIter = nivel.getMaxIteracoes();
        String dirSudoku = formatarDiretorioSudoku(sudoku, nivel);

        // Executar testes com diferentes algoritmos
        executarTesteAlgoritmo("Algoritmo SaturBFS", HEURISTIC_BREADTH_FIRST_SEARCH_SATURATED, maxIter, dirSudoku);
        executarTesteAlgoritmo("Algoritmo BFS", HEURISTIC_BREADTH_FIRST_SEARCH, maxIter, dirSudoku);
        executarTesteAlgoritmo("Algoritmo DFS", HEURISTIC_DEPTH_FIRST_SEARCH, maxIter, dirSudoku);
    }

    // Formata o caminho do diretório do Sudoku de acordo com o grau e nível
    private static String formatarDiretorioSudoku(Tipo sudoku, Niveis nivel) {
        return String.format("sudokus/%dX%d/%s/sudoku%dX%d_%s",
                sudoku.getGrau(), sudoku.getGrau(),
                nivel.getNivel(), sudoku.getGrau(), sudoku.getGrau(), nivel.getNivel());
    }

    // Executa o teste de um algoritmo específico com um número máximo de
    // iterações
    private static void executarTesteAlgoritmo(String nomeAlgoritmo, int heuristica, int maxIter, String dirSudoku) {
        System.out.println(String.format("**********  %s  **********", nomeAlgoritmo));
        executeTestMultiStart(heuristica, maxIter, dirSudoku);
    }

    public static void executeTestMultiStart(int optionHeuristic, int maxIteration, String sudokuFileDirectory) {
        int numberOfSudokuEachLevel = 10;
        int numberOfAttempts = 10;
        int numberOfHits = 0;
        int nextSudoku = 1;
        double mediumTime = 0;
        boolean isSolution;
        int contSolutionsOfSudokuEachLevel = 0;

        while (nextSudoku <= numberOfSudokuEachLevel) {
            int attempt = 1;
            isSolution = false;

            //Tempo ínicio
            double begin = Calendar.getInstance().getTimeInMillis();

            while (attempt <= numberOfAttempts) {
                boolean status = false;

                if (optionHeuristic == HEURISTIC_BREADTH_FIRST_SEARCH_SATURATED) {
                    status = multiStartBreadthFirstSearchSaturated(
                            String.format("%s_%s", sudokuFileDirectory, nextSudoku),
                            maxIteration
                    );
                } else if (optionHeuristic == HEURISTIC_BREADTH_FIRST_SEARCH) {
                    status = multiStartBreadthFirstSearch(
                            String.format("%s_%s", sudokuFileDirectory, nextSudoku),
                            maxIteration
                    );
                } else if (optionHeuristic == HEURISTIC_DEPTH_FIRST_SEARCH) {
                    status = multiStartDepthFirstSearch(
                            String.format("%s_%s", sudokuFileDirectory, nextSudoku),
                            maxIteration
                    );
                }

                if (status) {
                    numberOfHits++;
                    double end = 0;
                    if (isSolution == false) {
                        end = Calendar.getInstance().getTimeInMillis();
                        mediumTime += ((end - begin) / 1000);
                    }
                    isSolution = true;
                }
                attempt++;
            }
            if (isSolution) {
                contSolutionsOfSudokuEachLevel++;
            }
            nextSudoku++;
        }

        mediumTime /= contSolutionsOfSudokuEachLevel;
        double percentagem = (numberOfHits * 100) / (double) (numberOfSudokuEachLevel * numberOfAttempts);

        System.out.println(String.format("|  Tempo medio: %.4f segundos", mediumTime));
        System.out.println(String.format("|  Percentagem de solucoes validas: %.2f", percentagem) + "%");
    }

    public static boolean multiStartBreadthFirstSearchSaturated(String sudokuFileDirectory, int maxIter) {
        int numberAttemptsMultiStart = 1;

        while (numberAttemptsMultiStart <= maxIter) {
            readSudokuFile(sudokuFileDirectory);
            Boolean status = saturBFS();
            if (status) {
                return true;
            }
            numberAttemptsMultiStart++;
        }
        return false;
    }

    public static boolean multiStartBreadthFirstSearch(String sudokuFileDirectory, int maxIter) {
        int numberAttemptsMultiStart = 1;

        while (numberAttemptsMultiStart <= maxIter) {
            readSudokuFile(sudokuFileDirectory);
            Boolean status = BFS();
            if (status) {
                return true;
            }
            numberAttemptsMultiStart++;
        }
        return false;
    }

    public static boolean multiStartDepthFirstSearch(String sudokuFileDirectory, int maxIter) {
        int numberAttemptsMultiStart = 1;

        while (numberAttemptsMultiStart <= maxIter) {
            readSudokuFile(sudokuFileDirectory);
            Boolean status = DFS();
            if (status) {
                return true;
            }
            numberAttemptsMultiStart++;
        }
        return false;
    }

    public static Integer numbersColoredVertex() {
        Integer cont = 0;
        for (Integer corVertex : corVertice) {
            if (corVertex != null) {
                cont++;
            }
        }
        return cont;
    }

    public static void readSudokuFile(String nomeArquivo) {
        corVertice = new Integer[numVertices];

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(nomeArquivo + ".txt"))) {
            String line;
            int cont = 0;

            // Leitura linha por linha do arquivo
            while ((line = bufferedReader.readLine()) != null && !line.trim().isEmpty()) {
                String[] values = line.split("-");

                for (String value : values) {
                    // Converte valor para Integer e ignora zeros
                    Integer parsedValue = Integer.valueOf(value.trim());
                    if (parsedValue != 0) {
                        corVertice[cont] = parsedValue;
                    }
                    cont++;

                    // Verifica se atingiu o limite de vértices para evitar overflow
                    if (cont >= numVertices) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + nomeArquivo + ".txt");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter um valor do arquivo: valor inválido.");
            e.printStackTrace();
        }
    }

    //SaturBFS - Algoritmo Proposto - Busca largura usando Saturação para todos os vértices
    public static Boolean saturBFS() {
        Integer verticeInicio = highestSaturationVertex();
        Integer cor = colorirVertice(verticeInicio, grauSudoku);
        corVertice[verticeInicio] = cor;
        Integer[][] matrizSolucao = new Integer[grauSudoku][grauSudoku];

        List<Integer> fila = new ArrayList<>();
        fila.add(verticeInicio);

        while (!fila.isEmpty()) {
            Integer vertice = fila.get(0);
            fila.remove(0);

            Integer verticeAdj = highestSaturationAdjacent(findAdjacentVertices(vertice));

            while (verticeAdj != null) {
                cor = colorirVertice(verticeAdj, grauSudoku);
                if (cor == null) {
                    //System.out.println("Não achou uma cor para o vértice "+verticeAdj);
                    return false;
                }
                corVertice[verticeAdj] = cor;
                fila.add(verticeAdj);

                verticeAdj = highestSaturationAdjacent(findAdjacentVertices(vertice));
            }
        }
        return true;
    }

    public static Boolean BFS() {
        Integer verticeInicio = highestSaturationVertex();
        Integer cor = colorirVertice(verticeInicio, grauSudoku);
        corVertice[verticeInicio] = cor;

        List<Integer> fila = new ArrayList<>();
        fila.add(verticeInicio);

        while (!fila.isEmpty()) {
            Integer vertice = fila.get(0);
            fila.remove(0);

            for (Integer verticeAdj : findAdjacentVertices(vertice)) {
                if (corVertice[verticeAdj] == null) {
                    cor = colorirVertice(verticeAdj, grauSudoku);
                    if (cor == null) {
                        //System.out.println("Não achou uma cor para o vértice "+verticeAdj);
                        return false;
                    }
                    corVertice[verticeAdj] = cor;
                    fila.add(verticeAdj);
                }
            }
        }

        return true;
    }

    public static Boolean DFS() {
        Integer verticeInicio = highestSaturationVertex();
        Integer cor = colorirVertice(verticeInicio, grauSudoku);
        corVertice[verticeInicio] = cor;

        Stack<Integer> pilha = new Stack<>();
        pilha.push(verticeInicio);

        while (!pilha.isEmpty()) {
            Integer v = pilha.pop();

            for (Integer verticeAdj : findAdjacentVertices(v)) {
                if (corVertice[verticeAdj] == null) {
                    cor = colorirVertice(verticeAdj, grauSudoku);
                    if (cor == null) {
                        //System.out.println("Não achou uma cor para o vértice "+verticeAdj);
                        return false;
                    }
                    corVertice[verticeAdj] = cor;

                    pilha.push(v);
                    pilha.push(verticeAdj);
                    break;
                }
            }
        }
        return true;
    }

    public static Integer highestSaturationAdjacent(List<Integer> vertices) {
        Integer[] quantAdjacentesColoridos = new Integer[numVertices];
        int indexVertice = -1;
        int saturacaoMaxima = -1;

        List<Integer> verticesMesmaSaturacao = new ArrayList<>();

        // Primeiro loop: calcular a saturação de cada vértice não colorido
        for (Integer v : vertices) {
            if (corVertice[v] == null) {  // Vértice ainda não colorido
                int grauSaturacao = grauSaturacaoVertice(v);
                quantAdjacentesColoridos[v] = grauSaturacao;

                // Atualizar o vértice de maior saturação
                if (grauSaturacao > saturacaoMaxima) {
                    saturacaoMaxima = grauSaturacao;
                    indexVertice = v;

                    // Limpar a lista, pois temos um novo máximo
                    verticesMesmaSaturacao.clear();
                    verticesMesmaSaturacao.add(v);
                } else if (grauSaturacao == saturacaoMaxima) {
                    // Se a saturação é igual ao máximo, adicionamos o vértice à lista
                    verticesMesmaSaturacao.add(v);
                }
            }
        }

        // Se houver mais de um vértice com a mesma saturação, selecionar um aleatoriamente
        if (verticesMesmaSaturacao.size() > 1) {
            indexVertice = verticesMesmaSaturacao.get(new Random().nextInt(verticesMesmaSaturacao.size()));
        }

        return indexVertice == -1 ? null : indexVertice;
    }

    public static Integer highestSaturationVertex() {
        Integer[] saturacaoVertices = new Integer[numVertices];
        int saturacaoMaxima = -1;
        List<Integer> verticesComMaiorSaturacao = new ArrayList<>();

        // Iterar sobre todos os vértices para calcular suas saturações
        for (int v = 0; v < numVertices; v++) {
            if (corVertice[v] == null) { // Se o vértice ainda não foi colorido
                int grauSaturacao = grauSaturacaoVertice(v);
                saturacaoVertices[v] = grauSaturacao;

                // Se encontramos um vértice com maior saturação
                if (grauSaturacao > saturacaoMaxima) {
                    saturacaoMaxima = grauSaturacao;
                    verticesComMaiorSaturacao.clear(); // Limpar a lista de vértices com a saturação anterior
                    verticesComMaiorSaturacao.add(v);
                } else if (grauSaturacao == saturacaoMaxima) {
                    // Se a saturação é igual à saturação máxima, adicionar o vértice à lista
                    verticesComMaiorSaturacao.add(v);
                }
            }
        }

        // Se houver vértices com a mesma saturação, escolher um aleatoriamente
        if (!verticesComMaiorSaturacao.isEmpty()) {
            return verticesComMaiorSaturacao.get(new Random().nextInt(verticesComMaiorSaturacao.size()));
        }

        return null; // Retorna null se nenhum vértice puder ser selecionado
    }

    public static Integer grauSaturacaoVertice(Integer vertice) {
        Integer contCores = 0;
        List<Integer> adjacentes = findAdjacentVertices(vertice);
        for (Integer adjacente : adjacentes) {
            if (corVertice[adjacente] != null) {
                contCores++;
            }
        }
        return contCores;
    }

    public static Integer colorirVertice(Integer vertice, Integer totalCores) {
        List<Integer> coresValidas = new ArrayList<>();

        // Verificar quais cores podem ser usadas para o vértice
        for (int cor = 1; cor <= totalCores; cor++) {
            if (podeColorir(vertice, cor)) {
                coresValidas.add(cor);
            }
        }

        // Se não houver cores disponíveis, retorne null
        if (coresValidas.isEmpty()) {
            return null;
        }

        // Selecionar uma cor aleatória do conjunto de cores válidas
        return coresValidas.get(new Random().nextInt(coresValidas.size()));
    }

    public static boolean podeColorir(Integer vertice, Integer cor) {
        // Itera sobre os vértices adjacentes do vértice fornecido
        for (Integer adjacente : findAdjacentVertices(vertice)) {
            // Verifica se algum vértice adjacente já está colorido com a mesma cor
            if (corVertice[adjacente] != null && corVertice[adjacente].equals(cor)) {
                return false; // Se a cor for encontrada, retorna false
            }
        }
        return true; // Se nenhuma cor conflitante for encontrada, retorna true
    }

    public static List<Integer> findAdjacentVertices(Integer vertice) {
        List<Integer> adjacentVertices = new ArrayList<>();

        // Itera sobre todos os vértices para encontrar aqueles que são adjacentes ao vértice fornecido
        for (int i = 0; i < numVertices; i++) {
            if (matrizAdjacencia[vertice][i] == 1) {
                adjacentVertices.add(i);
            }
        }

        return adjacentVertices;
    }

    public static Integer[][] gerarMatrizAdjacencia(int sudokuDegree, int linhasBloco, int colunasBloco) {
        int numbersVertex = sudokuDegree * sudokuDegree;
        int contVertex = 0;

        Integer[][] vertexLabelMatrix = new Integer[sudokuDegree][sudokuDegree];
        Integer[][] adjacencyMatrix = new Integer[numbersVertex][numbersVertex];

        for (int i = 0; i < sudokuDegree; i++) {
            for (int j = 0; j < sudokuDegree; j++) {
                vertexLabelMatrix[i][j] = contVertex;
                contVertex++;
            }
        }

        for (int i = 0; i < numbersVertex; i++) {
            for (int j = 0; j < numbersVertex; j++) {
                if (j >= (i / sudokuDegree) * sudokuDegree && j < (i / sudokuDegree) * sudokuDegree + sudokuDegree) {
                    adjacencyMatrix[i][j] = 1;
                } else if (j % sudokuDegree == i % sudokuDegree) {
                    adjacencyMatrix[i][j] = 1;
                } else {
                    adjacencyMatrix[i][j] = 0;
                }
            }

        }

        List<Integer> verticesAdjacentes = new ArrayList<>();

        int inicioColuna = 0;
        for (int i = colunasBloco; i <= sudokuDegree; i += colunasBloco) {
            int inicioLinha = 0;
            for (int f = linhasBloco; f <= sudokuDegree; f += linhasBloco) {

                for (int m = inicioLinha; m < f; m++) {

                    for (int k = inicioColuna; k < i; k++) {

                        Integer vertice = vertexLabelMatrix[m][k];
                        verticesAdjacentes.add(vertice);

                        //System.out.print(" "+vertice);
                    }
                }
                //System.out.println(" ");

                //Ligar os vértices
                for (int l = 0; l < verticesAdjacentes.size(); l++) {
                    int v1 = verticesAdjacentes.get(l);
                    for (int j = l; j < verticesAdjacentes.size(); j++) {
                        int v2 = verticesAdjacentes.get(j);
                        adjacencyMatrix[v1][v2] = adjacencyMatrix[v2][v1] = 1;
                    }
                }
                verticesAdjacentes.clear();

                inicioLinha = f;
            }
            inicioColuna = i;
        }

        for (int i = 0; i < numbersVertex; i++) {
            adjacencyMatrix[i][i] = 0;
        }

        return adjacencyMatrix;

    }

    public static void printMatriz(Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(" " + matrix[i][j]);
            }
            System.out.println("");
        }
    }

    public static void printMatrizPorBlocos(Integer[][] matrix) {
        int n = matrix.length;  // Tamanho da matriz (ex: 9 para Sudoku 9x9)
        int tamanhoBloco = (int) Math.sqrt(n);  // Calcula o tamanho dos blocos (ex: 3 para Sudoku 9x9)

        for (int i = 0; i < n; i++) {
            // Imprime separador horizontal de blocos
            if (i % tamanhoBloco == 0 && i != 0) {
                System.out.println("-".repeat(n + (tamanhoBloco - 1) * 2));
            }

            for (int j = 0; j < n; j++) {
                // Imprime separador vertical de blocos
                if (j % tamanhoBloco == 0 && j != 0) {
                    System.out.print(" | ");
                }
                // Imprime o valor da célula
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();  // Quebra de linha após cada linha da matriz
        }
    }

}
